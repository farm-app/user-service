package ru.rtln.userservice.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.rtln.userservice.repository.base.ExtendedRepositoryImpl;

@EnableJpaAuditing
@EntityScan({"ru.rtln.userservice.entity", "ru.rtln.userservice.converter", })
@EnableJpaRepositories(
        basePackages = "ru.rtln.userservice.repository",
        repositoryBaseClass = ExtendedRepositoryImpl.class
)
@Configuration
public class DatabaseConfiguration {
}