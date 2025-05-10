package ru.rtln.userservice.service;

import org.springframework.validation.annotation.Validated;
import ru.rtln.userservice.model.PermissionModel;
import ru.rtln.userservice.model.PermissionUserModel;

import java.util.List;

@Validated
public interface PermissionService {

    List<PermissionModel> getAllPermissions();

    void deletePermissionById(Long permissionId);

    List<Long> getUsersWithPermission(String permission);

    List<Long> setPermissionToUsers(PermissionUserModel request);

    void removePermissionFromUser(String permissionName, Long userId, boolean refresh);

    void removePermissionFromUsers(PermissionUserModel request);

    void removePermissionsFromInactiveUsers();
}
