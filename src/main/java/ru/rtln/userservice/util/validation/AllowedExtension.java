package ru.rtln.userservice.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.rtln.userservice.util.validator.FileExtensionValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileExtensionValidator.class)
@Target({ElementType.PARAMETER})
public @interface AllowedExtension {

    FileExtensionValidator.FileType[] allowed();

    /**
     * Message to present if validation fails.
     */
    String message() default "File extension is invalid";

    /**
     * Action groups before which validation needs to be performed.
     */
    Class<?>[] groups() default {};

    /**
     * Meta information used in validation.
     */
    Class<? extends Payload>[] payload() default {};
}
