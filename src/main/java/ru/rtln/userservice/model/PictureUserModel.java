package ru.rtln.userservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PictureUserModel {

    private String profilePictureUrl;

    private List<String> additionalProfilePictureUrls;

    private Long profilePictureId;

    private List<Long> additionalProfilePictureIds;

    public PictureUserModel(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
