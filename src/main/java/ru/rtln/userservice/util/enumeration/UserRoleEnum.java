package ru.rtln.userservice.util.enumeration;

import lombok.Getter;
import ru.rtln.userservice.entity.Role;

@Getter
public enum UserRoleEnum {

    USER(1L),
    ADMIN(2L);

    private final Long id;
    private final Role role;

    UserRoleEnum(Long id) {
        this.role = new Role(id);
        this.id = id;
    }

    public static boolean isUser(Role role) {
        if (role == null) return false;
        return USER.getId().equals(role.getId());
    }

    public static boolean isAdmin(Role role) {
        if (role == null) return true;
        return ADMIN.getId().equals(role.getId());
    }
}
