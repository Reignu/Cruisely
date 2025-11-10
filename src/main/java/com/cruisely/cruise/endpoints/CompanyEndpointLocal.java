package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.auth.dto.BusinessWorkerDto;
import com.cruisely.cruise.dto.companies.AddCompanyDto;
import com.cruisely.cruise.dto.companies.CompanyLightDto;
import com.cruisely.cruise.dto.companies.CompanyDto;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

/**
 * Interface responsible for collecting mapped DTO objects into model objects related to companies
 * and invoking business logic methods with those mapped objects.
 */
@Local
public interface CompanyEndpointLocal extends TransactionalEndpoint {
    /**
     * Returns a list of DTO objects presenting company information necessary to create a user with BusinessWorker access level
     *
     * @return list of DTO objects representing company information required to create a BusinessWorker user
     */
    List<CompanyLightDto> getCompaniesInfo() throws BaseAppException;

    /**
     * Returns the list of company employees
     *
     * @param companyName Company name
     * @return DTO representation of a company employee
     * @throws BaseAppException base application exception thrown when the company is not found
     *                          or business rules are violated
     */
    List<BusinessWorkerDto> getBusinessWorkersForCompany(String companyName) throws BaseAppException;

    /**
     * Returns a list of DTO objects presenting company information
     *
     * @return list of DTO objects presenting company information
     */
    List<CompanyDto> getAllCompanies() throws BaseAppException;

    /**
     * Method used to add a new company
     * @param addCompanyDto AddCompanyDto object containing information from the user required to create a new company
     * @throws BaseAppException base application exception
     */
    void addCompany(AddCompanyDto addCompanyDto) throws BaseAppException;

    /**
     * Retrieves company metadata
     *
     * @param nip NIP of the company selected for metadata
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    MetadataDto getCompanyMetadata(long nip) throws BaseAppException;
}
