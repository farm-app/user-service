package ru.rtln.userservice.mapper;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import ru.rtln.userservice.entity.Permission;
import ru.rtln.userservice.model.PermissionModel;
import ru.rtln.userservice.util.Transliterator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class PermissionMapper {

    public List<PermissionModel> fromEntities(@NotNull Collection<Permission> permissions) {
        return permissions.stream()
                .sorted(Comparator.comparing(Permission::getId))
                .map(this::fromEntity)
                .toList();
    }

    public List<String> fromEntitiesToStringNames(@Nullable Collection<Permission> permissions) {
        return CollectionUtils.emptyIfNull(permissions).stream()
                .sorted(Comparator.comparing(Permission::getId))
                .map(Permission::getName)
                .toList();
    }

    public PermissionModel fromEntity(Permission permission) {
        return PermissionModel.builder()
                .id(permission.getId())
                .name(permission.getName())
                .displayName(permission.getDisplayName())
                .build();
    }

    public Permission toEntity(PermissionModel permission) {
        String displayName = permission.getDisplayName();
        return new Permission(
                Transliterator.cyrillicToLatin(displayName),
                displayName
        );
    }
}
