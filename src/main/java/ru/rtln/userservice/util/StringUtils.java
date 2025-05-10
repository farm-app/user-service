package ru.rtln.userservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public static boolean isNullOrEmpty(String value) {
        if (value == null) {
            return true;
        }
        return value.trim().isEmpty();
    }
}
