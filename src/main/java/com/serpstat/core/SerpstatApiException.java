package com.serpstat.core;

import lombok.Getter;

@Getter
public class SerpstatApiException extends Exception {

    private final Integer errorCode;
    private final String errorType;

    public SerpstatApiException(String message) {
        super(message);
        this.errorCode = null;
        this.errorType = null;
    }

    public SerpstatApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.errorType = null;
    }

    public SerpstatApiException(String message, Integer errorCode, String errorType) {
        super(message);
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
}
