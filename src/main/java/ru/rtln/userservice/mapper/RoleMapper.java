package ru.rtln.userservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.rtln.userservice.entity.Role;
import ru.rtln.userservice.entity.User;
import ru.rtln.userservice.model.AuthenticationUserModel;
import ru.rtln.userservice.model.RoleModel;

@RequiredArgsConstructor
@Component
public class RoleMapper {

    private final PermissionMapper permissionMapper;

    public RoleModel fromEntity(Role role) {
        return RoleModel.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(permissionMapper.fromEntities(role.getPermissions()))
                .build();
    }

    public RoleModel fromUserEntity(User user) {
        return fromEntity(user.getRole());
    }

    public AuthenticationUserModel fromEntityToAuthenticationUserModel(User user) {
        var permissions = user.getRole().getPermissions();
        var permissionStringNames = permissionMapper.fromEntitiesToStringNames(permissions);

        return AuthenticationUserModel.builder()
                .id(user.getId())
                .username(user.getUsername())
                .permissions(permissionStringNames)
                .city(user.getCity())
                .build();
    }
}