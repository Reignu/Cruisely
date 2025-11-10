package com.cruisely.exceptions;

import static com.cruisely.common.I18n.*;

/**
 * Class representing exceptions in accounts facade
 */
public class AccountFacadeException extends FacadeException {
    public AccountFacadeException(String message) {
        super(message);
    }

    public AccountFacadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AccountFacadeException loginReserved(Throwable cause) throws AccountFacadeException {
        throw new AccountFacadeException(LOGIN_RESERVED_ERROR, cause);
    }

    public static AccountFacadeException emailReserved(Throwable cause) throws AccountFacadeException {
        throw new AccountFacadeException(EMAIL_RESERVED_ERROR, cause);
    }

    public static AccountFacadeException userNotExists(Throwable cause) throws AccountFacadeException {
        throw new AccountFacadeException(USER_NOT_EXISTS_ERROR, cause);
    }
}
