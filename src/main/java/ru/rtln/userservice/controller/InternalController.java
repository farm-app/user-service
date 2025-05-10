package ru.rtln.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.rtln.common.model.SuccessModel;
import ru.rtln.userservice.config.properties.ProfileProperties;
import ru.rtln.userservice.model.AuthenticationUserModel;
import ru.rtln.userservice.model.PermissionUserModel;
import ru.rtln.userservice.model.UserModel;
import ru.rtln.userservice.scheduler.FileCleanupScheduler;
import ru.rtln.userservice.scheduler.KeycloakRefreshScheduler.UpdateKeycloakUserWithoutTransactionListenerEvent;
import ru.rtln.userservice.service.PermissionService;
import ru.rtln.userservice.service.RoleService;
import ru.rtln.userservice.service.UserService;
import ru.rtln.userservice.util.StringUtils;

import java.util.List;

import static ru.rtln.userservice.scheduler.UsersRefreshScheduler.RefreshUsersEvent;

@RequiredArgsConstructor
@RequestMapping("/internal")
@RestController
public class InternalController {

    private final UserService userService;

    private final RoleService roleService;

    private final PermissionService permissionService;

    private final ApplicationEventPublisher eventPublisher;

    private final FileCleanupScheduler fileCleanupScheduler;

    @ConditionalOnProperty(name = ProfileProperties.KEYCLOAK_ENABLED, havingValue = "true")
    @PostMapping("/user-info/keycloak/refresh")
    public SuccessModel<String> updateKeycloakUsersData() {
        eventPublisher.publishEvent(new UpdateKeycloakUserWithoutTransactionListenerEvent());
        return SuccessModel.okSuccessModel("Successful update keycloak user data", "updateKeycloakUserData");
    }

    @GetMapping("/user-info/{email}")
    public SuccessModel<AuthenticationUserModel> getAuthenticatedUserInternalInfo(@PathVariable String email) {
        var response = roleService.getAuthenticationUserModelByEmail(email);
        return SuccessModel.okSuccessModel(response, "getAuthenticatedUserInternalInfo");
    }

    @GetMapping("/users/{userId}/exists")
    public SuccessModel<Boolean> checkUserExistsById(@PathVariable Long userId) {
        var response = userService.existsById(userId);
        return SuccessModel.okSuccessModel(response, "checkUserExistsById");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public SuccessModel<UserModel> saveUserInternal(@RequestBody UserModel userModel) {
        var response = userService.saveUserInternal(userModel);
        return SuccessModel.createdSuccessModel(response, "saveUserInternal");
    }

    @PostMapping("/files/cleanup")
    public SuccessModel<String> deleteUnusedFiles() {
        fileCleanupScheduler.deleteUnusedFiles();
        return SuccessModel.okSuccessModel("Successfully deleted unused files", "deleteUnusedFiles");
    }

    @PostMapping("/users/refresh")
    public SuccessModel<String> refreshUsersData() {
        eventPublisher.publishEvent(new RefreshUsersEvent());
        return SuccessModel.okSuccessModel("User data updated successfully", "refreshUsersData");
    }

}
