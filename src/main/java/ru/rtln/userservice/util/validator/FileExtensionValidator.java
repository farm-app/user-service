package ru.rtln.userservice.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.rtln.userservice.util.validation.AllowedExtension;

import java.util.Arrays;
import java.util.List;

public class FileExtensionValidator implements ConstraintValidator<AllowedExtension, MultipartFile> {

    private List<String> allowedExtensions;

    public enum FileType {
        PNG,
        JPG,
        JPEG
    }

    @Override
    public void initialize(AllowedExtension constraintAnnotation) {
        this.allowedExtensions = Arrays.stream(constraintAnnotation.allowed())
                .map(FileType::name)
                .toList();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile == null) {
            return true;
        }
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        return extension != null && allowedExtensions.contains(extension.toUpperCase());
    }
}
