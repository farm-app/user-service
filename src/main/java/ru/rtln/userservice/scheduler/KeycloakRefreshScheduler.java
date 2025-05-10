package ru.rtln.userservice.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.rtln.userservice.config.properties.ProfileProperties;
import ru.rtln.userservice.entity.Permission;
import ru.rtln.userservice.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = ProfileProperties.KEYCLOAK_ENABLED, havingValue = "true")
@Slf4j
@Service
public class KeycloakRefreshScheduler {

    private final Keycloak keycloak;
    private final String realm;
    private final UserRepository userRepository;

    public KeycloakRefreshScheduler(Keycloak keycloak,
                                    @Value("${settings.keycloak.realm}") String realm,
                                    UserRepository userRepository) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @PostConstruct
    public void initSync() {
        updateUsersData();
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "${scheduler.cron.keycloak-users-refresh}")
    public void updateUsersData() {
        RealmResource keycloakRealm = keycloak.realm(realm);

        userRepository.findAllWithRole()
                .forEach(user -> {
                    try {
                    Optional<UserRepresentation> keycloakUser = keycloakRealm.users().search(user.getUsername())
                                .stream()
                                .filter(u -> u.getUsername().equals(user.getUsername()))
                                .findFirst();
                        if (keycloakUser.isPresent()) {
                            UserRepresentation userRepresentation = keycloakUser.get();
                            Map<String, List<String>> attributes = new HashMap<>(userRepresentation.getAttributes());
                            if (user.getId() != null) {
                                attributes.put("userId", List.of(user.getId().toString()));
                            }
                            if (user.getCity() != null) {
                                attributes.put("city", List.of(user.getCity()));
                            }
                            if (user.getRole() != null && user.getRole().getPermissions() != null) {
                                List<String> permissions = user.getRole().getPermissions().stream()
                                        .map(Permission::getName)
                                        .collect(Collectors.toList());
                                attributes.put("permissions", permissions);
                            }
                            userRepresentation.setAttributes(attributes);
                            var userResource = keycloakRealm.users().get(userRepresentation.getId());
                            userResource.update(userRepresentation);
                        } else {
                            log.warn("User with username=[{}] not found in keycloak users", user.getUsername());
                        }
                    } catch (Exception e) {
                        log.error("An error occurred during the user update process", e);
                    }
                });
    }

    public record UpdateKeycloakUserEvent() { }

    public record UpdateKeycloakUserWithoutTransactionListenerEvent() { }

    @Async
    @TransactionalEventListener(value = UpdateKeycloakUserEvent.class)
    public void updateKeycloakUsers() {
        updateUsersData();
    }

    @Async
    @Transactional(readOnly = true)
    @EventListener(value = UpdateKeycloakUserWithoutTransactionListenerEvent.class)
    public void updateKeycloakUsersWithoutTransactionListener() {
        updateUsersData();
    }
}
