package ru.rtln.userservice.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import ru.rtln.userservice.entity.ProfilePicture;
import ru.rtln.userservice.model.FileModel;
import ru.rtln.userservice.model.PictureUserModel;

@Validated
public interface ProfilePictureService {

    FileModel getPictureById(@NotNull Long pictureId);

    PictureUserModel getUserPictureById(@NotNull Long userId);

    PictureUserModel saveUserProfilePictures(
            @NotNull Long userId,
            MultipartFile profilePicture,
            MultipartFile[] additionalProfilePictures
    );

    PictureUserModel updateProfilePicture(
            @NotNull Resource requestPicture,
            @NotNull String contentType,
            @NotNull Long profilePictureIdPersisted
    );

    void deleteUserPicture(@NotNull Long pictureId, @NotNull Long userId);

    Resource getImageFromIntranet(@NotNull String email);

    void downloadProfilePictureFromIntranet(
            @NotNull String email,
            @NotNull Long userId
    );

    ProfilePicture generateNewProfilePicture(@NotNull Long userId);

    ProfilePicture generateNewProfilePicture();
}
