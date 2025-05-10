package ru.rtln.userservice.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class Constants {
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String DEFAULT_IMAGES_PATH = "classpath:/static";

    public static final String[] INTERNAL_ENDPOINTS = {
            "/users/{userId}"
    };
    public static final String[] EXTERNAL_ENDPOINTS = {
            "/permissions/**",
            "/internal/**",
            "/roles/**",
            "/users/**",
    };

    public static final Pattern VORONEZH_PATTERN = Pattern.compile("^.*(Воронеж|воронеж).*$");
}