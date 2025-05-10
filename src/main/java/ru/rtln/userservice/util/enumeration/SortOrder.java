package ru.rtln.userservice.util.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SortOrder {

    ASC,
    DESC,
    ;

    @Override
    @JsonValue
    public String toString() {
        return name().toLowerCase();
    }
}
