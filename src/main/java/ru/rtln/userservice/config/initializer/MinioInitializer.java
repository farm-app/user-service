package ru.rtln.userservice.config.initializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import ru.rtln.userservice.config.properties.MinioProperties;
import ru.rtln.userservice.service.ObjectStoreService;
import ru.rtln.userservice.util.enumeration.BucketType;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static ru.rtln.userservice.util.Constants.DEFAULT_IMAGES_PATH;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MinioInitializer {

    private final ResourceLoader resourceLoader;
    private final ObjectStoreService objectStoreService;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        initializeBuckets();
        fillingBotBucket();
    }

    private void initializeBuckets() {
        for (final var bucket : minioProperties.getBuckets().values()) {
            objectStoreService.createBucket(bucket);
        }
        log.info("Completion of minio buckets initialization");
    }

    private void fillingBotBucket() {
        final var bucket = minioProperties.getBucket(BucketType.BOT_PICTURE);
        final var pictureName = "bot_default.jpg";
        final var picturePath = DEFAULT_IMAGES_PATH + "/" + pictureName;
        if (!objectStoreService.isFileExists(pictureName, bucket)) {
            final var defaultPicture = resourceLoader.getResource(picturePath);
            objectStoreService.uploadFile(pictureName, defaultPicture, IMAGE_JPEG_VALUE, bucket);
        }
    }
}