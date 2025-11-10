package com.cruisely.cruise.managers;

import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.Account;
import com.cruisely.entities.auth.accesslevels.Administrator;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.auth.accesslevels.Moderator;
import com.cruisely.entities.cruise.Attraction;
import com.cruisely.entities.cruise.Company;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.CompanyMangerException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.auth.endpoints.converters.AccountMapper;
import com.cruisely.cruise.facades.CompanyFacadeMow;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

import static javax.ejb.TransactionAttributeType.MANDATORY;
import static com.cruisely.common.I18n.OPERATION_NOT_AUTHORIZED_ERROR;

/**
 * Class that manages business logic for companies
 */
@Stateful
@TransactionAttribute(MANDATORY)
@Interceptors(TrackingInterceptor.class)
public class CompanyManager extends BaseManagerMow implements CompanyManagerLocal {

    @Inject
    private CompanyFacadeMow companyFacadeMow;

    @Override
    public List<Company> getAllCompanies() throws BaseAppException {
        return companyFacadeMow.findAll();
    }

    @RolesAllowed("getBusinessWorkersForCompany")
    @Override
    public List<BusinessWorker> getBusinessWorkersForCompany(String companyName) throws BaseAppException {
        Account currentUser = getCurrentUser();

        //checking if businessWorker is neither Admin nor Moderator and wants to check workers from his own company
        if (currentUser.getAccessLevels().stream()
                .noneMatch(accessLevel -> accessLevel instanceof Moderator || accessLevel instanceof Administrator)
        ) {

            BusinessWorker businessWorker = (BusinessWorker) AccountMapper.getAccessLevel(currentUser, AccessLevelType.BUSINESS_WORKER);

            if (!businessWorker.getCompany().getName().equals(companyName)) {
                throw new CompanyMangerException(OPERATION_NOT_AUTHORIZED_ERROR);
            }
        }

        return companyFacadeMow.getBusinessWorkersByCompanyName(companyName);
    }

    @RolesAllowed("addCompany")
    @Override
    public void addCompany(Company company) throws BaseAppException {
        Account moderator = getCurrentUser();
        setCreatedMetadata(moderator, company, company.getAddress());

        companyFacadeMow.create(company);
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public Company findByNIP(long nip) throws BaseAppException {
        return companyFacadeMow.findByNIP(nip);
    }


}
