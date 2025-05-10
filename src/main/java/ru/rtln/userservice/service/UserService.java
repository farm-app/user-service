package ru.rtln.userservice.service;

import org.springframework.validation.annotation.Validated;
import ru.rtln.userservice.model.AuthenticationUserModel;
import ru.rtln.userservice.model.UserModel;

import java.util.List;

@Validated
public interface UserService {

    UserModel getUserById(Long userId);

    List<UserModel> getAllUsers();

    boolean existsById(Long userId);

    void saveUser(AuthenticationUserModel userModel);

    void saveUsers(List<AuthenticationUserModel> userModels);

    UserModel saveUserInternal(UserModel userModel);

    void updateUser(AuthenticationUserModel userModel);

    void updateUsers(List<AuthenticationUserModel> userModels);

    void updateRedisUsers();

    void updateUser(Long currentUserId, UserModel request);

    void deleteUser(Long userId);
}
