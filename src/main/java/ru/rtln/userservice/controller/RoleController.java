package ru.rtln.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rtln.common.model.SuccessModel;
import ru.rtln.userservice.config.security.meta.HasPermissionAuthority;
import ru.rtln.userservice.model.RoleModel;
import ru.rtln.userservice.service.RoleService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/roles")
@RestController
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public SuccessModel<List<RoleModel>> getAllRoles() {
        var response = roleService.getAllRoles();
        return SuccessModel.okSuccessModel(response, "getAllRoles");
    }

    @GetMapping("/users/{userId}")
    public SuccessModel<RoleModel> getUserRole(@PathVariable Long userId) {
        var response = roleService.getUserRoleByUserId(userId);
        return SuccessModel.okSuccessModel(response, "getUserRole");
    }

    @PutMapping("/{roleId}/users/{userId}")
    @HasPermissionAuthority
    public SuccessModel<RoleModel> setRoleToUser(
            @PathVariable Long roleId,
            @PathVariable Long userId
    ) {
        var response = roleService.setRoleToUser(roleId, userId);
        return SuccessModel.okSuccessModel(response, "setRoleToUser");
    }

    @PutMapping("/{roleId}")
    @HasPermissionAuthority
    public SuccessModel<RoleModel> updateRole(
            @PathVariable Long roleId,
            @RequestBody RoleModel request
    ) {
        request.setId(roleId);
        var response = roleService.updateRole(request);
        return SuccessModel.okSuccessModel(response, "updateRole");
    }
}
