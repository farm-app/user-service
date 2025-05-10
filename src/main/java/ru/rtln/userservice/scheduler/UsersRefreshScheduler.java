package ru.rtln.userservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.rtln.userservice.config.initializer.UsersInitializer;
import ru.rtln.userservice.service.PermissionService;
import ru.rtln.userservice.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Profile("!test")
@Component
public class UsersRefreshScheduler {

    private final UsersInitializer usersInitializer;
    private final UserService userService;
    private final PermissionService permissionService;

    @Transactional
    @Scheduled(cron = "${scheduler.cron.users-refresh}")
    public void refreshUsers() {
        userService.updateUsers(usersInitializer.getAllUsers());
        log.info("Successful scheduled user refresh");
    }

    @Transactional
    @Scheduled(cron = "${scheduler.cron.users-permissions-refresh}")
    public void refreshPermissions() {
        permissionService.removePermissionsFromInactiveUsers();
        log.info("Successful scheduled user permissions refresh");
    }

    public record RefreshRedisUsersEvent() {}
    public record RefreshUsersEvent() {}

    @Async
    @TransactionalEventListener(value = RefreshRedisUsersEvent.class)
    public void updateRedisUsers() {
        userService.updateRedisUsers();
    }

    @Async
    @Transactional
    @EventListener(value = RefreshUsersEvent.class)
    public void updateUsers() {
        refreshUsers();
    }
}
