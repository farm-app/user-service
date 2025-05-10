package ru.rtln.userservice.util.exception;

import java.text.MessageFormat;

public class FileException extends RuntimeException {

    protected final Code code;

    protected FileException(Code code, String msg) {
        this(code, null, msg);
    }

    protected FileException(Code code, Throwable e, String msg) {
        super(msg, e);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public enum Code {

        INVALID_CONTENT_TYPE("Invalid content type"),
        FILE_IS_NOT_IMAGE("File is not an image"),
        ;

        /**
         * Pattern {0} - code description, {1} - message
         */
        private static final String EXCEPTION_MESSAGE_PATTERN = "{0}: {1}";
        private final String description;

        Code(String description) {
            this.description = "%s (%s)".formatted(name(), description);
        }

        public FileException get() {
            return new FileException(this, description);
        }

        public FileException get(Throwable e) {
            String errorMessage = MessageFormat.format(EXCEPTION_MESSAGE_PATTERN, this.description, e.getMessage());
            return new FileException(this, e, errorMessage);
        }

        public FileException get(Throwable e, String msg) {
            String errorMessage = MessageFormat.format(EXCEPTION_MESSAGE_PATTERN, this.description, msg);
            return new FileException(this, e, errorMessage);
        }
    }
}
