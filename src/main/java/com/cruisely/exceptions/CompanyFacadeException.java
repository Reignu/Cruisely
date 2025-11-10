package com.cruisely.exceptions;

import static com.cruisely.entities.cruise.Company.COMPANY_NAME_UNIQUE_CONSTRAINT;
import static com.cruisely.entities.cruise.Company.NIP_UNIQUE_CONSTRAINT;

/**
 * Class representing company facade exceptions
 */
public class CompanyFacadeException extends FacadeException {

    public CompanyFacadeException(String message) {
        super(message);
    }

    public CompanyFacadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public static CompanyFacadeException nipNameReserved(Throwable cause) throws CompanyFacadeException {
        throw new CompanyFacadeException(NIP_UNIQUE_CONSTRAINT, cause);
    }

    public static CompanyFacadeException companyNameReserved(Throwable cause) throws CompanyFacadeException {
        throw new CompanyFacadeException(COMPANY_NAME_UNIQUE_CONSTRAINT, cause);
    }
}
