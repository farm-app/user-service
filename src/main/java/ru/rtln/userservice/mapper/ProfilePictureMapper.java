package ru.rtln.userservice.mapper;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.rtln.userservice.entity.ProfilePicture;
import ru.rtln.userservice.entity.User;
import ru.rtln.userservice.model.PictureUserModel;
import ru.rtln.userservice.service.ObjectStoreService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProfilePictureMapper {

    private final ObjectStoreService objectStoreService;

    @Value("${minio.buckets.profile-picture}")
    private String profilePictureBucket;

    public PictureUserModel fromUserEntity(User user) {
        Long profilePictureId = user.getProfilePicture().getId();

        List<String> additionalProfilePictureUrls = user.getProfilePictures().stream()
                .filter(ProfilePicture::isActive)
                .map(ProfilePicture::getId)
                .filter(id -> !id.equals(profilePictureId))
                .map(id -> objectStoreService.getFile(id, profilePictureBucket))
                .toList();

        return PictureUserModel.builder()
                .profilePictureUrl(objectStoreService.getFile(profilePictureId, profilePictureBucket))
                .additionalProfilePictureUrls(additionalProfilePictureUrls)
                .build();
    }

    public PictureUserModel fromIds(
            @NotNull Long profilePictureId,
            @NotNull List<Long> additionalProfilePictureIds
    ) {
        List<String> additionalProfilePictureUrls = additionalProfilePictureIds.stream()
                .map(id -> objectStoreService.getFile(id, profilePictureBucket))
                .toList();

        return PictureUserModel.builder()
                .profilePictureUrl(objectStoreService.getFile(profilePictureId, profilePictureBucket))
                .additionalProfilePictureUrls(additionalProfilePictureUrls)
                .build();
    }
}
