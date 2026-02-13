package com.satishlabs.auth.exception;

import com.satishlabs.auth.constants.ErrorCodes;

/**
 * Business exception that carries an error code for consistent API responses.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCodes errorCode;
    private final String overrideMessage;

    public BusinessException(ErrorCodes errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.overrideMessage = null;
    }

    public BusinessException(ErrorCodes errorCode, String overrideMessage) {
        super(overrideMessage != null ? overrideMessage : errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.overrideMessage = overrideMessage;
    }

    public BusinessException(ErrorCodes errorCode, Object... formatArgs) {
        super(String.format(errorCode.getDefaultMessage(), formatArgs));
        this.errorCode = errorCode;
        this.overrideMessage = null;
    }

    public ErrorCodes getErrorCode() {
        return errorCode;
    }

    public String getMessageForResponse() {
        return overrideMessage != null ? overrideMessage : getMessage();
    }
}
