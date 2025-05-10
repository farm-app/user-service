package ru.rtln.userservice.util.enumeration;

import lombok.Getter;

@Getter
public enum BucketType {

    PROFILE_PICTURE("profile-picture"),
    BOT_PICTURE("bot-picture");

    private final String name;

    BucketType(String name) {
        this.name = name;
    }
}
