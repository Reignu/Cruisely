package com.cruisely.exceptions;

/**
 * Class representing exception thrown by CompanyManager class
 */
public class CompanyMangerException extends BaseAppException {
    public CompanyMangerException(String message) {
        super(message);
    }

    public CompanyMangerException(String message, Throwable cause) {
        super(message, cause);
    }
}
