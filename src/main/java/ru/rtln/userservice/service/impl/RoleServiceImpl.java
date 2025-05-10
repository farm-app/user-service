package ru.rtln.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rtln.userservice.entity.Permission;
import ru.rtln.userservice.entity.Role;
import ru.rtln.userservice.entity.User;
import ru.rtln.userservice.mapper.PermissionMapper;
import ru.rtln.userservice.mapper.RoleMapper;
import ru.rtln.userservice.model.AuthenticationUserModel;
import ru.rtln.userservice.model.PermissionModel;
import ru.rtln.userservice.model.RoleModel;
import ru.rtln.userservice.repository.PermissionRepository;
import ru.rtln.userservice.repository.RoleRepository;
import ru.rtln.userservice.repository.UserRepository;
import ru.rtln.userservice.service.RoleService;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static ru.rtln.userservice.util.exception.InternalException.Code.INTERNAL_EXCEPTION;
import static ru.rtln.userservice.util.exception.NotFoundException.Code.ROLE_NOT_FOUND;
import static ru.rtln.userservice.util.exception.NotFoundException.Code.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final Map<Long, Role> roleIdToRole;
    private final Map<String, Permission> permissionNameToPermission;

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> getAllRoles() {
        return roleIdToRole.values().stream()
                .sorted(Comparator.comparing(Role::getId))
                .map(roleMapper::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleModel getUserRoleByUserId(Long userId) {
        return userRepository.findByIdWithRole(userId)
                .map(roleMapper::fromUserEntity)
                .orElseThrow(USER_NOT_FOUND::get);
    }

    @Override
    public AuthenticationUserModel getAuthenticationUserModelByEmail(String email) {
        return userRepository.findByEmailWithRole(email)
                .map(roleMapper::fromEntityToAuthenticationUserModel)
                .orElseThrow(USER_NOT_FOUND::get);
    }

    @Override
    @Transactional
    public RoleModel setRoleToUser(Long roleId, Long userId) {
        User userPersisted = userRepository.findByIdWithRole(userId)
                .orElseThrow(USER_NOT_FOUND::get);

        return Optional.ofNullable(roleIdToRole.get(roleId))
                .map(userPersisted::setRole)
                .map(roleMapper::fromEntity)
                .orElseThrow(ROLE_NOT_FOUND::get);
    }

    @Override
    @Transactional
    public RoleModel updateRole(RoleModel request) {
        Role role = roleRepository.findByIdWithPermissions(request.getId())
                .map(Role::clearPermissions)
                .orElseThrow(ROLE_NOT_FOUND::get);

        return Optional.of(role)
                .map(entity -> prepareRolePermissionsBeforeRoleSave(entity, request))
                .map(roleRepository::save)
                .map(roleMapper::fromEntity)
                .orElseThrow(INTERNAL_EXCEPTION::get);
    }

    private Role prepareRolePermissionsBeforeRoleSave(Role role, RoleModel request) {
        Set<Permission> rolePermissions = new HashSet<>();
        Set<Permission> permissionsTransient = new HashSet<>();

        for (PermissionModel permissionModel : request.getPermissions()) {
            var permissionPersisted = permissionNameToPermission.get(permissionModel.getDisplayName());

            if (permissionPersisted == null) {
                permissionsTransient.add(permissionMapper.toEntity(permissionModel));
            } else {
                rolePermissions.add(permissionPersisted);
            }
        }

        rolePermissions.addAll(
                permissionRepository.saveAll(permissionsTransient)
        );

        return role.setPermissions(rolePermissions);
    }
}