package ru.rtln.userservice.util.exception;

import java.text.MessageFormat;

public class AlreadyHasException extends RuntimeException {

    protected final Code code;

    protected AlreadyHasException(Code code, String msg) {
        this(code, null, msg);
    }

    protected AlreadyHasException(Code code, Throwable e, String msg) {
        super(msg, e);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public enum Code {

        USERNAME_ALREADY_EXISTS("User with specified username already exists"),
        EMAIL_ALREADY_EXISTS("User with specified email already exists"),
        USER_ALREADY_DELETED("User already deleted");

        /**
         * Pattern {0} - code description, {1} - message
         */
        private static final String EXCEPTION_MESSAGE_PATTERN = "{0}: {1}";
        private final String description;

        Code(String description) {
            this.description = "%s (%s)".formatted(name(), description);
        }

        public AlreadyHasException get() {
            return new AlreadyHasException(this, description);
        }

        public AlreadyHasException get(Throwable e) {
            String errorMessage = MessageFormat.format(EXCEPTION_MESSAGE_PATTERN, this.description, e.getMessage());
            return new AlreadyHasException(this, e, errorMessage);
        }

        public AlreadyHasException get(Throwable e, String msg) {
            String errorMessage = MessageFormat.format(EXCEPTION_MESSAGE_PATTERN, this.description, msg);
            return new AlreadyHasException(this, e, errorMessage);
        }
    }
}
