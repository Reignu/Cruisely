package com.cruisely.cruise.managers;

import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.cruise.Company;
import com.cruisely.exceptions.BaseAppException;

import javax.ejb.Local;
import java.util.List;

/**
 * Class that manages business logic for companies
 */
@Local
public interface CompanyManagerLocal {
    /**
     * Retrieves all company objects from the database
     *
     * @return list of all companies
     */
    List<Company> getAllCompanies() throws BaseAppException;

    /**
     * Returns the list of company employees
     *
     * @param companyName Company name
     * @return Entity object of the access level for the given company employee
     * @throws BaseAppException base application exception, thrown when the company is not found
     *                          or business rules are violated
     */
    List<BusinessWorker> getBusinessWorkersForCompany(String companyName) throws BaseAppException;

    /**
     * Adds a new company
     * @param company Company object
     * @throws BaseAppException base application exception
     */
    void addCompany(Company company) throws BaseAppException;


    /**
     * Method that finds a company by NIP
     *
     * @param nip company's NIP
     * @return Company
     * @throws BaseAppException Base application exception
     */
    Company findByNIP(long nip) throws BaseAppException;
}
