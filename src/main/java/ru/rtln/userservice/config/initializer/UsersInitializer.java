package ru.rtln.userservice.config.initializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.rtln.common.model.SuccessModel;
import ru.rtln.userservice.model.AuthenticationUserModel;
import ru.rtln.userservice.redis.repository.RedisUserRepository;
import ru.rtln.userservice.repository.UserRepository;
import ru.rtln.userservice.service.UserService;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@Profile("!test")
@DependsOn("minioInitializer")
@Configuration
public class UsersInitializer {

    private final RestTemplate authRestTemplate;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RedisUserRepository redisUserRepository;

    @Transactional
    @PostConstruct
    public void init() {
        initUsersTable();
        updateUsersTables();
        log.info("Completion of user table initialization");
    }

    private void initUsersTable() {
        if (userRepository.count() != 0) {
            log.info("User table is not empty. Skipping initialization");
            return;
        }
        userService.saveUsers(getAllUsers());
    }

    private void updateUsersTables() {
        if (redisUserRepository.count() != 0) {
            log.info("RedisUser table is not empty. Skipping initialization");
            return;
        }
        userService.updateUsers(getAllUsers());
    }

    public List<AuthenticationUserModel> getAllUsers() {
        var usersResponse = authRestTemplate.exchange(
                "/api/auth/internal/users",
                GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<SuccessModel<List<AuthenticationUserModel>>>() {
                }
        );

        var usersResponseStatusCode = usersResponse.getStatusCode();
        var usersResponseBody = usersResponse.getBody();
        if (usersResponseStatusCode != OK || usersResponseBody == null || usersResponseBody.getData() == null) {
            log.error("Auth service is not available");
            return List.of();
        }

        return usersResponseBody.getData();
    }
}