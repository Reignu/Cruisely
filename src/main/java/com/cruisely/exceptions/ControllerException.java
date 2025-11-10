package com.cruisely.exceptions;

import com.cruisely.common.I18n;

/**
 * Class representing exception in the controller
 */
public class ControllerException extends BaseAppException {
    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerException(String message) {
        super(message);
    }

}
