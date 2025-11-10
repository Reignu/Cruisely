package com.cruisely.cruise.managers;

import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.Account;
import com.cruisely.entities.auth.accesslevels.Administrator;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.cruise.*;
import com.cruisely.exceptions.AccountManagerException;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.CruiseGroupManagerException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.auth.endpoints.converters.AccountMapper;
import com.cruisely.cruise.facades.CompanyFacadeMow;
import com.cruisely.cruise.facades.CruiseFacadeMow;
import com.cruisely.cruise.facades.CruiseGroupFacadeMow;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;
import static com.cruisely.common.IntegrityUtils.checkForOptimisticLock;


/**
 * Class managing business logic related to cruise groups
 */
@Stateful
@Interceptors(TrackingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CruiseGroupManager extends BaseManagerMow implements CruiseGroupManagerLocal {

    @Inject
    private CruiseGroupFacadeMow cruiseGroupFacadeMow;
    @Inject
    private CompanyFacadeMow companyFacadeMow;
    @Inject
    private CruiseFacadeMow cruiseFacadeMow;

    @RolesAllowed("addCruiseGroup")
    @Override
    public void addCruiseGroup(String companyName, String name, long number_of_seats, Double price, CruiseAddress start_address, List<CruisePicture> pictures, String description) throws BaseAppException {
        Company company = companyFacadeMow.findByName(companyName);
        Account account = getCurrentUser();
        BusinessWorker worker = (BusinessWorker) AccountMapper.getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);

        if (!worker.getCompany().getName().equals(company.getName())) {
            throw new CruiseGroupManagerException(OPERATION_NOT_AUTHORIZED_ERROR);
        }

        CruiseGroup cruisegroup = new CruiseGroup(company, start_address, name, number_of_seats, price, pictures, description);
        setCreatedMetadata(account, cruisegroup);
        setCreatedMetadata(account, start_address);

        for (CruisePicture picture : pictures) {
            setCreatedMetadata(getCurrentUser(), picture);
        }
        cruiseGroupFacadeMow.create(cruisegroup);
    }

    @RolesAllowed("changeCruiseGroup")
    @Override
    public void changeCruiseGroup(String name, long number_of_seats, Double price, CruiseAddress startAddress, long version, String description, CruisePicture picture, UUID uuid) throws BaseAppException {

        CruiseGroup cruiseGroup = cruiseGroupFacadeMow.findByUUID(uuid);
        checkForOptimisticLock(cruiseGroup, version);

        BusinessWorker worker = (BusinessWorker) AccountMapper.getAccessLevel(getCurrentUser(), AccessLevelType.BUSINESS_WORKER);

        if (!worker.getCompany().getName().equals(cruiseGroup.getCompany().getName())) {
            throw new CruiseGroupManagerException(OPERATION_NOT_AUTHORIZED_ERROR);
        }

        if (!cruiseFacadeMow.findByCruiseGroupUUID(cruiseGroup.getUuid()).isEmpty()) {
            throw new CruiseGroupManagerException(OPERATION_NOT_AUTHORIZED_ERROR);
        }

        cruiseGroup.setName(name);
        cruiseGroup.setNumberOfSeats(number_of_seats);
        cruiseGroup.setPrice(price);

        CruiseAddress targetAddress = cruiseGroup.getAddress();
        targetAddress.setStreet(startAddress.getStreet());
        targetAddress.setStreetNumber(startAddress.getStreetNumber());
        targetAddress.setHarborName(startAddress.getHarborName());
        targetAddress.setCityName(startAddress.getCityName());
        targetAddress.setCountryName(startAddress.getCountryName());

        cruiseGroup.setDescription(description);
        setUpdatedMetadata(cruiseGroup);
        setUpdatedMetadata(startAddress);

        if (!picture.getImg().isEmpty()) {
            if (cruiseGroup.getCruisePictures().isEmpty()) {
                setCreatedMetadata(getCurrentUser(), picture);
                cruiseGroup.getCruisePictures().add(picture);
            } else {
                cruiseGroup.getCruisePictures().get(0).setImg(picture.getImg());
            }
            setUpdatedMetadata(cruiseGroup.getCruisePictures().get(0));
        }

    }


    @RolesAllowed("getAllCruiseGroupList")
    @Override
    public List<CruiseGroup> getAllCruiseGroups() throws FacadeException {
        return cruiseGroupFacadeMow.findAll();
    }

    @RolesAllowed("getAllCruiseGroupList")
    @Override
    public List<Cruise> getCruiseBelongsToCruiseGroup(CruiseGroup cruiseGroup) throws BaseAppException {
        return cruiseGroupFacadeMow.findCruisesForCruiseGroup(cruiseGroup);
    }

    @RolesAllowed("deactivateCruiseGroup")
    @Override
    public void deactivateCruiseGroup(UUID uuid, long version) throws BaseAppException {
        CruiseGroup cruiseGroup = cruiseGroupFacadeMow.findByUUID(uuid);
        checkForOptimisticLock(cruiseGroup, version);

        Account account = getCurrentUser();

        if (!cruiseGroup.isActive()) {
            throw new AccountManagerException(CRUISE_GROUP_ALREADY_DEACTIVATED);
        }

        if (account.getAccessLevels().stream()
                .noneMatch(accessLevel -> accessLevel instanceof Administrator)) {

            BusinessWorker businessWorker = (BusinessWorker) AccountMapper.getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);

            if (cruiseGroup.getCompany().getNIP() != businessWorker.getCompany().getNIP()) {
                throw new CruiseGroupManagerException(BUSINESS_WORKER_DONT_OWN_CRUISE_GROUP);
            }
        }

        cruiseGroup.setActive(false);
        setUpdatedMetadata(cruiseGroup);
        cruiseGroupFacadeMow.edit(cruiseGroup);
    }

    @RolesAllowed("getCruiseGroupForBusinessWorker")
    @Override
    public List<CruiseGroup> getCruiseGroupForBusinessWorker(String companyName) throws BaseAppException {
        Company company = companyFacadeMow.findByName(companyName);
        Account account = getCurrentUser();

        BusinessWorker worker = (BusinessWorker) AccountMapper.getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);

        if (!worker.getCompany().getName().equals(company.getName())) {
            throw new CruiseGroupManagerException(OPERATION_NOT_AUTHORIZED_ERROR);
        }

        return cruiseGroupFacadeMow.getCruiseGroupForBusinessWorker(company);
    }

    @PermitAll
    @Override
    public CruiseGroup findByUUID(UUID uuid) throws BaseAppException {
        return cruiseGroupFacadeMow.findByUUID(uuid);
    }

}

