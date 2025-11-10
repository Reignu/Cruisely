package com.cruisely.exceptions;

import static com.cruisely.common.I18n.ETAG_IDENTITY_INTEGRITY_ERROR;

/**
 * Class representing exceptions thrown for ETag
 */
public class ETagException extends BaseAppException {
    public ETagException(String message, Throwable cause) {
        super(message, cause);
    }

    public ETagException(String message) {
        super(message);
    }

    public static ETagException etagIdentityIntegrity() throws ETagException {
        throw new ETagException(ETAG_IDENTITY_INTEGRITY_ERROR);
    }
}
