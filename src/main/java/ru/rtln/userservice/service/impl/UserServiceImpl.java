package ru.rtln.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rtln.userservice.entity.User;
import ru.rtln.userservice.mapper.UserMapper;
import ru.rtln.userservice.model.AuthenticationUserModel;
import ru.rtln.userservice.model.UserModel;
import ru.rtln.userservice.redis.repository.RedisUserRepository;
import ru.rtln.userservice.repository.UserRepository;
import ru.rtln.userservice.service.ProfilePictureService;
import ru.rtln.userservice.service.UserService;
import ru.rtln.userservice.util.enumeration.UserRoleEnum;
import ru.rtln.userservice.util.exception.AlreadyHasException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.rtln.userservice.util.exception.AlreadyHasException.Code.EMAIL_ALREADY_EXISTS;
import static ru.rtln.userservice.util.exception.AlreadyHasException.Code.USERNAME_ALREADY_EXISTS;
import static ru.rtln.userservice.util.exception.NotFoundException.Code.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final ProfilePictureService profilePictureService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisUserRepository redisUserRepository;

    @Value("${settings.excluded-get-usernames}")
    private Set<String> excludedFromGetUsernames;

    @Value("${settings.excluded-refresh-usernames}")
    private Set<String> excludedFromRefreshUsernames;

    @Override
    @Transactional(readOnly = true)
    public UserModel getUserById(Long userId) {
        userRepository.findByIdWithRole(userId);

        User user = userRepository.findByIdWithManyToOneFields(userId)
                .orElseThrow(USER_NOT_FOUND::get);

        return userMapper.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> getAllUsers() {
        return userRepository.findAllWithManyToOneFields().stream()
                .sorted(Comparator.comparing(User::getId))
                .map(userMapper::fromEntityToBriefView)
                .map(model -> excludedFromGetUsernames.contains(model.getUsername()) ? model.setActive(false) : model)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional
    public void saveUser(AuthenticationUserModel userModel) {
        var profilePicturePersisted = profilePictureService.generateNewProfilePicture();
        var userTransient = userMapper.toEntityFromAuthenticationUserModel(
                userModel,
                profilePicturePersisted
        );

        User userPersisted = userRepository.save(userTransient);
        profilePicturePersisted.setUser(userPersisted);

        redisUserRepository.save(userMapper.toRedisEntity(userPersisted));
    }

    @Override
    @Transactional
    public void saveUsers(List<AuthenticationUserModel> userModels) {
        userModels.forEach(this::saveUser);
    }

    @Override
    @Transactional
    public UserModel saveUserInternal(UserModel userModel) {
        String username = userModel.getUsername();
        String email = userModel.getEmail();

        if (userRepository.existsByUsername(username)) throw USERNAME_ALREADY_EXISTS.get();
        if (userRepository.existsByEmail(email)) throw EMAIL_ALREADY_EXISTS.get();

        var profilePicturePersisted = profilePictureService.generateNewProfilePicture();
        User userTransient = userMapper.toEntity(
                userModel,
                profilePicturePersisted
        );

        User userPersisted = userRepository.save(userTransient);
        profilePicturePersisted.setUser(userPersisted);

        return userMapper.fromEntityToBriefView(userPersisted);
    }

    @Override
    @Transactional
    public void updateUser(AuthenticationUserModel userModel) {
        String email = userModel.getEmail();
        Optional<User> userPersistedOpt = userRepository.findByEmail(email);

        if (userPersistedOpt.isEmpty()) {
            saveUser(userModel);
            return;
        }

        var userPersisted = userPersistedOpt.get();

        if (!userRepository.existsByUsername(userModel.getUsername())) {
            userPersisted.setUsername(userModel.getUsername());
        }

        if (!userRepository.existsByEmail(userModel.getEmail())) {
            userPersisted.setEmail(userModel.getEmail());
        }

        userMapper.mergeFromAuthenticationUserModel(userPersisted, userModel);
    }

    @Override
    @Transactional
    public void updateUsers(List<AuthenticationUserModel> userModels) {
        for (AuthenticationUserModel userModel : userModels) {
            if (excludedFromRefreshUsernames.contains(userModel.getUsername())) continue;
            updateUser(userModel);
        }

        userRepository.findAllWithRole();
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (excludedFromRefreshUsernames.contains(user.getUsername())) continue;

            var profilePicturePersisted = user.getProfilePicture();

            if (profilePicturePersisted == null) {
                user.setProfilePicture(profilePictureService.generateNewProfilePicture(user.getId()));
            }
        }

        for (User user : allUsers) {
            redisUserRepository.save(userMapper.toRedisEntity(user));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void updateRedisUsers() {
        userRepository.findAllWithRole();
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            redisUserRepository.save(userMapper.toRedisEntity(user));
        }
    }

    @Override
    @Transactional
    public void updateUser(Long currentUserId, UserModel request) {
        User userPersisted = userRepository.findById(currentUserId)
                .orElseThrow(USER_NOT_FOUND::get);

        userMapper.mergeFromUserModel(userPersisted, request);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User userPersisted = userRepository.findById(userId)
                .orElseThrow(USER_NOT_FOUND::get);
        if (!userPersisted.isActive()) {
            throw AlreadyHasException.Code.USER_ALREADY_DELETED.get();
        }
        userPersisted.setActive(false);
    }
}
