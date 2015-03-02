package it.alessandronatilla.preprocessing.lemmatizer.exceptions;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class EmptyEntryException extends RuntimeException {
    public EmptyEntryException() {
        super();
    }

    public EmptyEntryException(Throwable cause) {
        super(cause);
    }

    public EmptyEntryException(String message) {
        super(message);
    }

    public EmptyEntryException(String message, Throwable cause) {
        super(message, cause);
    }

    protected EmptyEntryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
