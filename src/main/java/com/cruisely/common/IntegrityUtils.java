package com.cruisely.common;

import com.cruisely.entities.common.BaseEntity;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.ETagException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.security.EntityIdentitySignerVerifier;
import com.cruisely.security.SignableEntity;

public class IntegrityUtils {
    private IntegrityUtils() {
    }

    public static void checkForOptimisticLock(BaseEntity entity, long version) throws BaseAppException {
        if (entity.getVersion() != version) {
            throw FacadeException.optimisticLock();
        }
    }

    public static void checkEtagIntegrity(SignableEntity entity, String etag) throws BaseAppException {
        if (!EntityIdentitySignerVerifier.verifyEntityIntegrity(entity, etag)) {
            throw ETagException.etagIdentityIntegrity();
        }
    }
}
