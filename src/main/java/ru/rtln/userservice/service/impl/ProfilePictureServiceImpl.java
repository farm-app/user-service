package ru.rtln.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.rtln.userservice.entity.ProfilePicture;
import ru.rtln.userservice.entity.User;
import ru.rtln.userservice.mapper.ProfilePictureMapper;
import ru.rtln.userservice.model.FileModel;
import ru.rtln.userservice.model.PictureUserModel;
import ru.rtln.userservice.repository.ProfilePictureRepository;
import ru.rtln.userservice.repository.UserRepository;
import ru.rtln.userservice.service.ObjectStoreService;
import ru.rtln.userservice.service.ProfilePictureService;
import ru.rtln.userservice.util.validator.FileValidator;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static ru.rtln.userservice.util.exception.NotFoundException.Code.PROFILE_PICTURE_NOT_FOUND;
import static ru.rtln.userservice.util.exception.NotFoundException.Code.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfilePictureServiceImpl implements ProfilePictureService {

    private final ProfilePictureRepository profilePictureRepository;
    private final UserRepository userRepository;
    private final ProfilePictureMapper profilePictureMapper;
    private final ObjectStoreService objectStoreService;
    private final RestTemplate intranetRestTemplate;

    @Value("${minio.buckets.profile-picture}")
    private String profilePictureBucket;

    @Override
    @Transactional(readOnly = true)
    public FileModel getPictureById(Long pictureId) {
        return profilePictureRepository.findById(pictureId)
                .filter(ProfilePicture::isActive)
                .map(entity -> objectStoreService.getFile(pictureId, profilePictureBucket))
                .map(FileModel::new)
                .orElseThrow(PROFILE_PICTURE_NOT_FOUND::get);
    }

    @Override
    @Transactional
    public PictureUserModel getUserPictureById(Long userId) {
        return userRepository.findByIdWithProfilePictures(userId)
                .map(profilePictureMapper::fromUserEntity)
                .orElseThrow(USER_NOT_FOUND::get);
    }

    @Override
    @Transactional
    public PictureUserModel saveUserProfilePictures(
            Long userId,
            MultipartFile requestProfilePicture,
            MultipartFile[] requestAdditionalProfilePictures
    ) {
        var userPersisted = userRepository.findByIdWithProfilePictures(userId)
                .orElseThrow(USER_NOT_FOUND::get);
        var profilePictureIdPersisted = userPersisted.getProfilePicture().getId();
        var profilePictureUrl = objectStoreService.getFile(profilePictureIdPersisted, profilePictureBucket);

        var pictureUserModel = new PictureUserModel(profilePictureUrl);
        if (requestProfilePicture != null) {
            pictureUserModel = updateProfilePicture(
                    requestProfilePicture.getResource(),
                    requestProfilePicture.getContentType(),
                    profilePictureIdPersisted
            );
        }

        return saveAdditionalProfilePictures(
                pictureUserModel,
                requestAdditionalProfilePictures,
                userId
        );
    }

    @Override
    @Transactional
    public void deleteUserPicture(Long pictureId, Long userId) {
        User userPersisted = userRepository.findByIdWithProfilePictures(userId)
                .orElseThrow(USER_NOT_FOUND::get);

        deletePicture(userPersisted, pictureId);
    }

    @Override
    public void downloadProfilePictureFromIntranet(String email, Long profilePictureId) {
        updateProfilePicture(getImageFromIntranet(email), IMAGE_JPEG_VALUE, profilePictureId);
    }

    @Override
    public Resource getImageFromIntranet(String email) {
        try {
            return intranetRestTemplate.getForObject(
                    "/api/avatar/%s?s=200".formatted(convertToMD5(email)),
                    Resource.class
            );
        } catch (HttpClientErrorException | NoSuchAlgorithmException e) {
            log.error("Http client exception", e);
            return null;
        }
    }

    private String convertToMD5(String string) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(string.getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }

    @Override
    public PictureUserModel updateProfilePicture(
            Resource requestPicture,
            String contentType,
            Long profilePictureIdPersisted
    ) {
        FileValidator.isFileValidImage(contentType, requestPicture);

        var pictureUrl = objectStoreService.uploadFile(
                profilePictureIdPersisted.toString(),
                requestPicture,
                contentType,
                profilePictureBucket
        );

        return new PictureUserModel(pictureUrl);
    }

    private PictureUserModel saveAdditionalProfilePictures(
            PictureUserModel pictureUserModel,
            MultipartFile[] requestAdditionalPictures,
            Long userId
    ) {
        if (requestAdditionalPictures != null) {
            List<Long> additionalPictureIds = new ArrayList<>();
            for (var requestPicture : requestAdditionalPictures) {
                FileValidator.isFileValidImage(requestPicture);

                var newPictureId = generateNewProfilePicture(userId).getId();
                additionalPictureIds.add(newPictureId);

                objectStoreService.uploadFile(newPictureId, requestPicture, profilePictureBucket);
            }

            pictureUserModel = profilePictureMapper.fromIds(
                    pictureUserModel.getProfilePictureId(),
                    additionalPictureIds
            );
        }
        return pictureUserModel;
    }

    @Override
    public ProfilePicture generateNewProfilePicture(Long userId) {
        var pictureTransient = new ProfilePicture(userId);
        return profilePictureRepository.save(pictureTransient);
    }

    @Override
    public ProfilePicture generateNewProfilePicture() {
        var pictureTransient = new ProfilePicture();
        return profilePictureRepository.save(pictureTransient);
    }

    private void deletePicture(User userPersisted, Long pictureId) {
        if (!profilePictureRepository.existsById(pictureId)) {
            throw PROFILE_PICTURE_NOT_FOUND.get();
        }

        ProfilePicture picture = userPersisted.getProfilePicture();
        if (picture.getId().equals(pictureId)) {
            profilePictureRepository.disableById(pictureId);
        }
    }
}
