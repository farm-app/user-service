package ru.rtln.userservice.util.validator;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import static ru.rtln.userservice.util.exception.FileException.Code.FILE_IS_NOT_IMAGE;
import static ru.rtln.userservice.util.exception.FileException.Code.INVALID_CONTENT_TYPE;

@UtilityClass
public class FileValidator {

    public static void isFileValidImage(MultipartFile multipartFile) {
        validateExistence(multipartFile);
        validateContentType(multipartFile.getContentType());
    }

    public static void isFileValidImage(String contentType, Resource resource) {
        validateExistence(resource);
        validateContentType(contentType);
    }

    private static void validateExistence(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw FILE_IS_NOT_IMAGE.get();
        }
    }

    private static void validateExistence(Resource resource) {
        if (!resource.exists()) {
            throw FILE_IS_NOT_IMAGE.get();
        }
    }

    public static void validateContentType(String contentType) {
        if (contentType == null) {
            return;
        }

        String[] split = contentType.split("/");
        if (split.length < 2) {
            throw INVALID_CONTENT_TYPE.get();
        }
        if (!split[0].equalsIgnoreCase("image")) {
            throw FILE_IS_NOT_IMAGE.get();
        }
    }
}