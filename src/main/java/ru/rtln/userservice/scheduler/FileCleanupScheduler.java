package ru.rtln.userservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.rtln.userservice.repository.ProfilePictureRepository;
import ru.rtln.userservice.repository.extension.DisabledIdFinderRepository;
import ru.rtln.userservice.service.ObjectStoreService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileCleanupScheduler {

    private final ProfilePictureRepository profilePictureRepository;
    private final ObjectStoreService objectStoreService;

    @Value("${scheduler.pictures-lifetime}")
    private Duration picturesLifetime;

    @Value("${minio.buckets.profile-picture}")
    private String profilePictureBucket;

    @Transactional
    @Scheduled(cron = "${scheduler.cron.file-clean-up}")
    public void deleteUnusedFiles() {
        deleteUnusedFilesInBucket(profilePictureRepository, profilePictureBucket);
        log.info("Successful scheduled deletion unused files");
    }

    private void deleteUnusedFilesInBucket(DisabledIdFinderRepository finderRepository, String bucket) {
        List<Long> idsToDelete = new ArrayList<>();
        for (Long disabledId : finderRepository.findAllDisabledIds(picturesLifetime)) {
            if (objectStoreService.deleteFile(disabledId, bucket)) {
                idsToDelete.add(disabledId);
            }
        }
        finderRepository.deleteAllByIdInBatch(idsToDelete);
    }
}
