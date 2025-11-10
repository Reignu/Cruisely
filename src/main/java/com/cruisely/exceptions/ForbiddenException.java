package com.cruisely.exceptions;

import static com.cruisely.common.I18n.APP_FORBIDDEN;

/**
 * Klasa reprezentująca wyjątek 403
 * Class representing exception 403
 */
public class ForbiddenException extends Exception { // to avoid overuse, will throw manually
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException forbidden() {
        return new ForbiddenException(APP_FORBIDDEN);
    }
}
