package ru.rtln.userservice.util;

import lombok.experimental.UtilityClass;

import java.security.Principal;

import static ru.rtln.userservice.util.exception.InternalException.Code.CAST_ERROR;

@UtilityClass
public class CastHelper {

    public static Long getLong(Object value) {
        if (value == null) {
            return null;
        }

        Long res;
        if (value instanceof Long valueLong) {
            res = valueLong;
        } else if (value instanceof Integer valueInt) {
            res = Long.valueOf(valueInt);
        } else {
            throw CAST_ERROR.get();
        }

        return res;
    }

    public static Long getCurrentUserId(Principal userId) {
        return Long.valueOf(userId.getName());
    }
}
