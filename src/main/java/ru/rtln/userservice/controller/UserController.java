package ru.rtln.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rtln.common.model.SuccessModel;
import ru.rtln.common.security.model.AuthenticatedUserModel;
import ru.rtln.userservice.config.security.meta.HasAdminAuthority;
import ru.rtln.userservice.model.UserModel;
import ru.rtln.userservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public SuccessModel<List<UserModel>> getAllUsers() {
        var response = userService.getAllUsers();
        return SuccessModel.okSuccessModel(response, "getAllUsers");
    }

    @GetMapping("/{userId}")
    public UserModel getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/user-info")
    public SuccessModel<UserModel> getAuthenticatedUserInfo(
            @AuthenticationPrincipal AuthenticatedUserModel authUser
    ) {
        var response = userService.getUserById(authUser.getId());
        return SuccessModel.okSuccessModel(response, "getAuthenticatedUserInfo");
    }

    @PutMapping
    public SuccessModel<String> updateUser(
            @AuthenticationPrincipal AuthenticatedUserModel authUser,
            @RequestBody UserModel request
    ) {
        userService.updateUser(authUser.getId(), request);
        return SuccessModel.okSuccessModel("User has been successfully updated", "updateUser");
    }

    @DeleteMapping("/{userId}")
    @HasAdminAuthority
    public SuccessModel<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return SuccessModel.okSuccessModel("User has been successfully deleted", "deleteUser");
    }

}