package ru.rtln.userservice.entity;

import java.util.Objects;

public enum Gender {
    MALE(1L, 'M'),
    FEMALE(2L, 'F');

    private final Long id;
    private final Character simpleName;

    Gender(Long id, Character simpleName) {
        this.id = id;
        this.simpleName = simpleName;
    }

    public static Gender getValueFromId(Long value) {
        if (value == null) {
            return null;
        }
        for (final Gender type : Gender.values()) {
            if (value.equals(type.id)) {
                return type;
            }
        }
        return null;
    }

    public static Gender getValueFromSimpleName(Character simpleName) {
        if (simpleName == null) {
            return null;
        }
        for (final Gender type : Gender.values()) {
            if (Objects.equals(Character.toUpperCase(simpleName), type.simpleName)) {
                return type;
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public String getSimpleName() {
        return simpleName.toString();
    }
}
