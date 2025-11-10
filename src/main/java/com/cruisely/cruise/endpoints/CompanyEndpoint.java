package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.BaseEndpoint;
import com.cruisely.common.mappers.MetadataMapper;
import com.cruisely.entities.cruise.Company;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.auth.dto.BusinessWorkerDto;
import com.cruisely.auth.endpoints.converters.AccountMapper;
import com.cruisely.cruise.dto.companies.AddCompanyDto;
import com.cruisely.cruise.dto.companies.CompanyLightDto;
import com.cruisely.cruise.dto.companies.CompanyDto;
import com.cruisely.cruise.endpoints.converters.CompanyMapper;
import com.cruisely.cruise.managers.CompanyManagerLocal;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 * Class that handles collecting mapped DTO objects to model objects related to companies and calls logic methods passing mapped objects.
 */
@Stateful
@TransactionAttribute(REQUIRES_NEW)
@Interceptors(TrackingInterceptor.class)
public class CompanyEndpoint extends BaseEndpoint implements CompanyEndpointLocal {
    @Inject
    private CompanyManagerLocal companyManager;

    @Override
    public List<CompanyLightDto> getCompaniesInfo() throws BaseAppException {
        return companyManager.getAllCompanies().stream().map(CompanyMapper::mapCompanyToCompanyLightDto).collect(Collectors.toList());
    }

    @RolesAllowed("getBusinessWorkersForCompany")
    @Override
    public List<BusinessWorkerDto> getBusinessWorkersForCompany(String companyName) throws BaseAppException {
        return companyManager.getBusinessWorkersForCompany(companyName).stream()
                .map(AccountMapper::toBusinessWorkerDto).collect(Collectors.toList());
    }

    @RolesAllowed("getAllCompanies")
    @Override
    public List<CompanyDto> getAllCompanies() throws BaseAppException {
        return companyManager.getAllCompanies().stream().map(CompanyMapper::mapCompanyToCompanyDto).collect(Collectors.toList());
    }

    @RolesAllowed("addCompany")
    @Override
    public void addCompany(AddCompanyDto addCompanyDto) throws BaseAppException {
        Company company = CompanyMapper.mapAddCompanyDtoToCompany(addCompanyDto);
        companyManager.addCompany(company);
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public MetadataDto getCompanyMetadata(long nip) throws BaseAppException {
        return MetadataMapper.toMetadataDto(companyManager.findByNIP(nip));
    }
}
