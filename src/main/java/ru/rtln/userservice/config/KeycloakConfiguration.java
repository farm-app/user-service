package ru.rtln.userservice.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfiguration {

    @Bean
    public Keycloak getKeycloakInstance(@Value("${settings.keycloak.uri}") String keycloakUrl,
                                        @Value("${settings.keycloak.realm}") String realm,
                                        @Value("${settings.keycloak.service-account.username}") String serviceAccountUsername,
                                        @Value("${settings.keycloak.service-account.password}") String serviceAccountPassword,
                                        @Value("${settings.keycloak.client-id}") String clientId,
                                        @Value("${settings.keycloak.client-secret}") String clientSecret
    ) {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm(realm)
                .clientId(clientId)
                .username(serviceAccountUsername)
                .password(serviceAccountPassword)
                .clientSecret(clientSecret)
                .build();
    }
}
