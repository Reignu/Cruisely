package com.cruisely.exceptions;

import com.cruisely.common.I18n;

/**
 * Class representing exceptions thrown for JWT token
 */
public class JWTException extends BaseAppException {

    public JWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public JWTException(String message) {
        super(message);
    }

    public static JWTException tokenExpired() throws JWTException {
        throw new JWTException(I18n.TOKEN_EXPIRED_ERROR);
    }
}
