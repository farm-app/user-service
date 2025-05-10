package ru.rtln.userservice.util.exception;

import java.text.MessageFormat;

public class DoesNotHaveException extends RuntimeException {

    protected final Code code;

    protected DoesNotHaveException(Code code, String msg) {
        this(code, null, msg);
    }

    protected DoesNotHaveException(Code code, Throwable e, String msg) {
        super(msg, e);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public enum Code {

        USER_DOES_NOT_HAVE_ACHIEVEMENT("User does not have specified achievement"),
        USER_DOES_NOT_HAVE_DELIVERY_ABILITY("User is not able to use the delivery");

        /**
         * Pattern {0} - code description, {1} - message
         */
        private static final String EXCEPTION_MESSAGE_PATTERN = "{0}: {1}";
        private final String description;

        Code(String description) {
            this.description = "%s (%s)".formatted(name(), description);
        }

        public DoesNotHaveException get() {
            return new DoesNotHaveException(this, description);
        }

        public DoesNotHaveException get(Throwable e) {
            String errorMessage = MessageFormat.format(EXCEPTION_MESSAGE_PATTERN, this.description, e.getMessage());
            return new DoesNotHaveException(this, e, errorMessage);
        }

        public DoesNotHaveException get(Throwable e, String msg) {
            String errorMessage = MessageFormat.format(EXCEPTION_MESSAGE_PATTERN, this.description, msg);
            return new DoesNotHaveException(this, e, errorMessage);
        }
    }
}
