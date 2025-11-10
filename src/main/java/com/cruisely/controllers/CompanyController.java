package com.cruisely.controllers;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.auth.dto.BusinessWorkerDto;
import com.cruisely.cruise.dto.companies.AddCompanyDto;
import com.cruisely.cruise.dto.companies.CompanyLightDto;
import com.cruisely.cruise.dto.companies.CompanyDto;
import com.cruisely.cruise.endpoints.CompanyEndpointLocal;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

@Path("/company")
@RequestScoped
public class CompanyController {
    @Inject
    private CompanyEndpointLocal companyEndpoint;

    /**
     * Retrieves information about companies
     *
     * @return List of companies
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/companies-info")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CompanyLightDto> getAllCompaniesInfo() throws BaseAppException {
        return tryAndRepeat(companyEndpoint, () -> companyEndpoint.getCompaniesInfo());
    }

    /**
     * Retrieves information about companies
     *
     * @return List of companies
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/companies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CompanyDto> getAllCompanies() throws BaseAppException {
        return tryAndRepeat(companyEndpoint, () -> companyEndpoint.getAllCompanies());
    }

    /**
     * Retrieves information about employees of a given company
     *
     * @param companyName Company name
     * @return List of company employees as DTO representations
     * @throws BaseAppException Base application exception thrown when the company is not found or business rules are violated
     */
    @GET
    @Path("/{companyName}/business-workers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BusinessWorkerDto> getBusinessWorkersForCompany(@PathParam("companyName") String companyName) throws BaseAppException {
        return tryAndRepeat(companyEndpoint, () -> companyEndpoint.getBusinessWorkersForCompany(companyName));
    }

    /**
     * Method responsible for adding a company by a moderator
     *
     * @param addCompanyDto DTO object containing information provided by the moderator
     * @throws BaseAppException Base application exception thrown in case of business rule violations
     */
    @POST
    @Path("/add-company")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addCompany(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid AddCompanyDto addCompanyDto) throws BaseAppException {
        tryAndRepeat(companyEndpoint, () -> companyEndpoint.addCompany(addCompanyDto));
    }

    /**
     * Retrieves company metadata
     *
     * @param nip NIP of the company selected for metadata
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/{nip}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getCompanyMetadata(@PathParam("nip") String nip) throws BaseAppException {
        try {
            long companyNIP = Long.parseLong(nip);
            return tryAndRepeat(companyEndpoint, () -> companyEndpoint.getCompanyMetadata(companyNIP));
        } catch (NumberFormatException e) {
            throw new MapperException(MAPPER_LONG_PARSE);
        }
    }
}
