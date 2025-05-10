package ru.rtln.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.rtln.common.model.SuccessModel;
import ru.rtln.common.security.model.AuthenticatedUserModel;
import ru.rtln.userservice.model.PictureUserModel;
import ru.rtln.userservice.service.ProfilePictureService;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    @GetMapping("/pictures/{pictureId}")
    public ResponseEntity<Void> getProfilePictureById(@PathVariable Long pictureId) {
        var response = profilePictureService.getPictureById(pictureId);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(response.getUrl()))
                .build();
    }

    @GetMapping("/{userId}/pictures")
    public SuccessModel<PictureUserModel> getUserProfilePicturesById(@PathVariable Long userId) {
        var response = profilePictureService.getUserPictureById(userId);
        return SuccessModel.okSuccessModel(response, "getUserProfilePicturesById");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/pictures")
    public SuccessModel<PictureUserModel> saveUserProfilePictures(
            @AuthenticationPrincipal AuthenticatedUserModel authUser,
            @RequestParam(required = false) MultipartFile profilePicture,
            @RequestParam(required = false) MultipartFile[] additionalProfilePictures
    ) {
        var response = profilePictureService.saveUserProfilePictures(
                authUser.getId(),
                profilePicture,
                additionalProfilePictures
        );
        return SuccessModel.createdSuccessModel(response, "saveUserProfilePictures");
    }

    @DeleteMapping("/pictures/{pictureId}")
    public SuccessModel<String> deleteUserProfilePicture(
            @AuthenticationPrincipal AuthenticatedUserModel authUser,
            @PathVariable Long pictureId
    ) {
        profilePictureService.deleteUserPicture(pictureId, authUser.getId());
        return SuccessModel.deletedSuccessModel("deleteUserProfilePicture");
    }

}
