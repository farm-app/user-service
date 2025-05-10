package ru.rtln.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rtln.userservice.entity.Permission;
import ru.rtln.userservice.entity.Role;
import ru.rtln.userservice.entity.User;
import ru.rtln.userservice.mapper.PermissionMapper;
import ru.rtln.userservice.model.PermissionModel;
import ru.rtln.userservice.model.PermissionUserModel;
import ru.rtln.userservice.repository.PermissionRepository;
import ru.rtln.userservice.repository.RoleRepository;
import ru.rtln.userservice.repository.UserRepository;
import ru.rtln.userservice.scheduler.KeycloakRefreshScheduler;
import ru.rtln.userservice.scheduler.UsersRefreshScheduler;
import ru.rtln.userservice.service.PermissionService;
import ru.rtln.userservice.util.enumeration.UserRoleEnum;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.rtln.userservice.util.exception.NotFoundException.Code.PERMISSION_NOT_FOUND;
import static ru.rtln.userservice.util.exception.NotFoundException.Code.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements PermissionService {

    private final Map<String, Permission> permissionNameToPermission;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<PermissionModel> getAllPermissions() {
        return permissionNameToPermission.values().stream()
                .sorted(Comparator.comparing(Permission::getId))
                .map(permissionMapper::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public void deletePermissionById(Long permissionId) {
        if (!permissionRepository.existsById(permissionId)) {
            throw PERMISSION_NOT_FOUND.get();
        }

        permissionRepository.deletePermissionRoleByPermissionId(permissionId);
        permissionRepository.deleteById(permissionId);

        eventPublisher.publishEvent(new UsersRefreshScheduler.RefreshRedisUsersEvent());
        eventPublisher.publishEvent(new KeycloakRefreshScheduler.UpdateKeycloakUserEvent());
    }

    @Override
    public List<Long> getUsersWithPermission(String permission) {
        return permissionRepository.findAllUsersWithPermission(permission.toUpperCase());
    }

    @Override
    @Transactional
    public List<Long> setPermissionToUsers(PermissionUserModel request) {
        String permissionName = request.getPermissionName();
        log.info("setPermissionToUsers. Permission Name: {}", Optional.ofNullable(permissionName));
        var permission = permissionRepository.findByName(permissionName)
                .orElseThrow(PERMISSION_NOT_FOUND::get);

        permissionRepository.deletePermissionRoleByPermissionIdWithoutAdminRole(permission.getId());

        request.getUserIds().forEach(userId -> setPermissionToUser(userId, permission));

        eventPublisher.publishEvent(new UsersRefreshScheduler.RefreshRedisUsersEvent());
        eventPublisher.publishEvent(new KeycloakRefreshScheduler.UpdateKeycloakUserEvent());

        return permissionRepository.findAllUsersWithPermission(permission.getName());
    }

    @Override
    @Transactional
    public void removePermissionFromUser(String permissionName, Long userId, boolean refresh) {
        var role = userRepository.findRealByIdWithRole(userId)
                .map(User::getRole)
                .orElseThrow(USER_NOT_FOUND::get);
        if (UserRoleEnum.isAdmin(role)) return;
        log.info("removePermissionFromUser. Permission Name: {}", Optional.ofNullable(permissionName));
        var permission = permissionRepository.findByName(permissionName)
                .orElseThrow(PERMISSION_NOT_FOUND::get);
        permissionRepository.deletePermissionRoleByPermissionIdAndRoleId(permission.getId(), role.getId());

        if (refresh) {
            eventPublisher.publishEvent(new UsersRefreshScheduler.RefreshRedisUsersEvent());
            eventPublisher.publishEvent(new KeycloakRefreshScheduler.UpdateKeycloakUserEvent());
        }
    }

    @Override
    @Transactional
    public void removePermissionFromUsers(PermissionUserModel request) {
        var permissionName = request.getPermissionName();
        for (var userId : request.getUserIds()) {
            removePermissionFromUser(permissionName, userId, false);
        }

        eventPublisher.publishEvent(new UsersRefreshScheduler.RefreshRedisUsersEvent());
        eventPublisher.publishEvent(new KeycloakRefreshScheduler.UpdateKeycloakUserEvent());
    }

    @Override
    @Transactional
    public void removePermissionsFromInactiveUsers() {
        permissionRepository.deletePermissionsFromInactiveUsers();
    }

    private void setPermissionToUser(Long userId, Permission permission) {
        userRepository.findByIdWithRole(userId)
                .map(User::getRole)
                .ifPresent(role -> {
                    if (UserRoleEnum.isUser(role)) {
                        role = roleRepository.save(new Role());
                        roleRepository.setRoleToUser(role, userId);
                    }
                    role.addPermission(permission);
                });
    }
}
