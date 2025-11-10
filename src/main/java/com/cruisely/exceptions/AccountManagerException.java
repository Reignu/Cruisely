package com.cruisely.exceptions;

/**
 * Class representing exception thrown by AccountManager class
 */
public class AccountManagerException extends BaseAppException {
    public AccountManagerException(String message) {
        super(message);
    }

    public AccountManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
