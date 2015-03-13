package it.alessandronatilla.preprocessing.exceptions;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class UnsupportedLanguageException extends RuntimeException {
    public UnsupportedLanguageException() {
        super();
    }

    public UnsupportedLanguageException(Throwable cause) {
        super(cause);
    }

    public UnsupportedLanguageException(String message) {
        super(message);
    }

    public UnsupportedLanguageException(String message, Throwable cause) {
        super(message, cause);
    }

    protected UnsupportedLanguageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
