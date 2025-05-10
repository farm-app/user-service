package ru.rtln.userservice.service;

import ru.rtln.userservice.model.AuthenticationUserModel;
import ru.rtln.userservice.model.RoleModel;

import java.util.List;

public interface RoleService {

    List<RoleModel> getAllRoles();

    RoleModel getUserRoleByUserId(Long userId);

    AuthenticationUserModel getAuthenticationUserModelByEmail(String email);

    RoleModel setRoleToUser(Long roleId, Long userId);

    RoleModel updateRole(RoleModel request);
}
