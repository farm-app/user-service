package ru.rtln.userservice.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rtln.userservice.config.properties.MinioProperties;

@Configuration
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        return new MinioClient.Builder()
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .endpoint(minioProperties.getUrl())
                .build();
    }
}