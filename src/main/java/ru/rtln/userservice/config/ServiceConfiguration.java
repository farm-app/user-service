package ru.rtln.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Configuration
public class ServiceConfiguration {

    @Bean
    public RestTemplate authRestTemplate(
            @Value("${internal.service.auth.url}") String authUri,
            @Value("${internal.api-key-header-name}") String apiKeyHeaderName,
            @Value("${internal.service.auth.secure-key}") String authSecretKeyValue,
            RestTemplateBuilder builder
    ) {
        return builder
                .rootUri(authUri)
                .defaultHeader(apiKeyHeaderName, authSecretKeyValue)
                .setConnectTimeout(Duration.of(5, SECONDS))
                .build();
    }
}
