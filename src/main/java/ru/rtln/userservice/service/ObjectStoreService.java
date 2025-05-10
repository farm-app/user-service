package ru.rtln.userservice.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStoreService {

    void createBucket(@NotNull String bucket);

    String getFile(@NotNull Long id, @NotNull String bucket);

    String getFile(@NotNull String id, @NotNull String bucket);

    String uploadFile(
            @NotNull Long id,
            @NotNull MultipartFile file,
            @NotNull String bucket
    );

    String uploadFile(
            @NotNull String id,
            @NotNull Resource resource,
            @NotNull String contentType,
            @NotNull String bucket
    );

    boolean deleteFile(@NotNull Long id, @NotNull String bucket);

    boolean isFileExists(@NotNull String name, @NotNull String bucket);
}
