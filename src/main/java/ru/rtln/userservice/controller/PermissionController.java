package ru.rtln.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rtln.common.model.SuccessModel;
import ru.rtln.userservice.config.security.meta.HasAdminAuthority;
import ru.rtln.userservice.config.security.meta.HasPermissionAuthority;
import ru.rtln.userservice.model.PermissionModel;
import ru.rtln.userservice.model.PermissionUserModel;
import ru.rtln.userservice.service.PermissionService;
import ru.rtln.userservice.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/permissions")
@RestController
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public SuccessModel<List<PermissionModel>> getAllPermissions() {
        var response = permissionService.getAllPermissions();
        return SuccessModel.okSuccessModel(response, "getAllPermissions");
    }

    @DeleteMapping("/{permissionId}")
    @HasPermissionAuthority
    public SuccessModel<String> deletePermissionById(@PathVariable Long permissionId) {
        permissionService.deletePermissionById(permissionId);
        return SuccessModel.deletedSuccessModel("deletePermissionById");
    }

    @GetMapping("/{permission}/users")
    @HasAdminAuthority
    public SuccessModel<List<Long>> getUsersWithPermission(
            @PathVariable String permission
    ) {
        var response = permissionService.getUsersWithPermission(permission);
        return SuccessModel.okSuccessModel(response, "getUsersWithPermission");
    }

    @PostMapping("/{permission}/users")
    @HasAdminAuthority
    public SuccessModel<List<Long>> setPermissionToUsers(
            @PathVariable String permission,
            @RequestBody PermissionUserModel request
    ) {
        request.setPermissionName(permission);
        var response = permissionService.setPermissionToUsers(request);
        return SuccessModel.okSuccessModel(response, "setPermissionToUsers");
    }

    @DeleteMapping("/{permission}/users/{userId}")
    @HasAdminAuthority
    public SuccessModel<String> removePermissionFromUser(
            @PathVariable String permission,
            @PathVariable Long userId
    ) {
        if (!StringUtils.isNullOrEmpty(permission)) {
            permission = permission.toUpperCase();
        }
        permissionService.removePermissionFromUser(permission, userId, true);
        return SuccessModel.deletedSuccessModel("removePermissionFromUser");
    }

    @DeleteMapping("/{permission}/users")
    @HasAdminAuthority
    public SuccessModel<String> removePermissionFromUsers(
            @PathVariable String permission,
            @RequestBody PermissionUserModel request
    ) {
        request.setPermissionName(permission);
        permissionService.removePermissionFromUsers(request);
        return SuccessModel.deletedSuccessModel("removePermissionFromUsers");
    }
}
