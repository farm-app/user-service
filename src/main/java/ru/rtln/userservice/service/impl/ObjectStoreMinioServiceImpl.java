package ru.rtln.userservice.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.rtln.userservice.service.ObjectStoreService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ObjectStoreMinioServiceImpl implements ObjectStoreService {

    private final MinioClient minioClient;

    @Value("${minio.external-url}")
    private String minioExternalUrl;

    @Override
    public void createBucket(String bucket) {
        try {
            var bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build();

            if (minioClient.bucketExists(bucketExistsArgs)) {
                log.info("Bucket {} already exists", bucket);
                return;
            }

            var makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build();

            minioClient.makeBucket(makeBucketArgs);

            String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Action": "s3:GetObject",
                                "Effect": "Allow",
                                "Principal": "*",
                                "Resource": "arn:aws:s3:::*"
                            }
                        ]
                    }""";

            var setBucketPolicyArgs = SetBucketPolicyArgs.builder()
                    .bucket(bucket)
                    .config(policy)
                    .build();

            minioClient.setBucketPolicy(setBucketPolicyArgs);

            log.debug("Bucket {} has been created", bucket);
        } catch (Exception e) {
            log.error("An error occurred while creating bucket", e);
        }
    }

    @Override
    public String getFile(Long id, String bucket) {
        return getFile(id.toString(), bucket);
    }

    @Override
    public String getFile(String id, String bucket) {
        return "%s/%s/%s".formatted(minioExternalUrl, bucket, id);
    }

    @Override
    public String uploadFile(Long id, MultipartFile file, String bucket) {
        return uploadFile(id.toString(), file.getResource(), file.getContentType(), bucket);
    }

    @Override
    public String uploadFile(String id, Resource resource, String contentType, String bucket) {
        try {
            var putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(id)
                    .stream(resource.getInputStream(), resource.contentLength(), -1)
                    .contentType(contentType)
                    .build();

            minioClient.putObject(putObjectArgs);

            return getFile(id, bucket);
        } catch (Exception e) {
            log.error("An error occurred while uploading file", e);
            return null;
        }
    }

    @Override
    public boolean deleteFile(Long id, String bucket) {
        try {
            if (!isFileExists(id.toString(), bucket)) return true;

            var removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(id.toString())
                    .build();

            minioClient.removeObject(removeObjectArgs);

            return true;
        } catch (Exception e) {
            log.error("An error occurred while deleting file", e);
            return false;
        }
    }

    @Override
    public boolean isFileExists(String name, String bucket) {
        try {
            var statObjectArgs = StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(name)
                    .build();

            minioClient.statObject(statObjectArgs);

            return true;
        } catch (ErrorResponseException e) {
            log.error("File not found", e);
            return false;
        } catch (Exception e) {
            log.error("An error occurred while checking the existence of the file", e);
            return false;
        }
    }
}
