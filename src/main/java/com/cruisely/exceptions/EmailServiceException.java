package com.cruisely.exceptions;

/**
 * Class representing exceptions for email service
 */
public class EmailServiceException extends BaseAppException {
    protected EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailServiceException(String message) {
        super(message);
    }
}
