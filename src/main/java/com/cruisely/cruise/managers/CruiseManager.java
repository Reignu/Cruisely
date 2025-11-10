package com.cruisely.cruise.managers;


import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.Account;
import com.cruisely.entities.auth.accesslevels.Administrator;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.cruise.Attraction;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.entities.cruise.CruiseGroup;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.CruiseManagerException;
import com.cruisely.auth.endpoints.converters.AccountMapper;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;
import static com.cruisely.common.IntegrityUtils.checkForOptimisticLock;

/**
 * Class that manages business logic for cruises (trips)
 */
@Interceptors(TrackingInterceptor.class)
@Stateful
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CruiseManager extends BaseManagerMow implements CruiseManagerLocal {
    @Inject
    private CruiseFacadeMow cruiseFacadeMow;
    @Inject
    private CruiseGroupFacadeMow cruiseGroupFacadeMow;

    @RolesAllowed("addCruise")
    @Override
    public void addCruise(Cruise cruise, UUID cruiseGroupUUID) throws BaseAppException {

        if (cruise.getStartDate().isBefore(LocalDateTime.now())) {
            throw new CruiseManagerException(START_DATE_BEFORE_CURRENT_DATE);
        }

        if (cruise.getStartDate().isAfter(cruise.getEndDate())) {
            throw new CruiseManagerException(START_DATE_AFTER_END_DATE);
        }

        CruiseGroup cruiseGroup = cruiseGroupFacadeMow.findByUUID(cruiseGroupUUID);
        if (!cruiseGroup.isActive()) {
            throw new CruiseManagerException(CRUISE_GROUP_INACTIVE);
        }
        cruise.setCruisesGroup(cruiseGroup);

        Account account = getCurrentUser();
        BusinessWorker businessWorker = (BusinessWorker) AccountMapper.getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);

        if (cruise.getCruisesGroup().getCompany().getNIP() != businessWorker.getCompany().getNIP()) {
            throw new CruiseManagerException(BUSINESS_WORKER_ADD_CRUISE_TO_BAD_COMPANY);
        }

        setCreatedMetadata(account, cruise);
        cruiseFacadeMow.create(cruise);
    }

    @RolesAllowed("deactivateCruise")
    @Override
    public void deactivateCruise(UUID uuid, Long version) throws BaseAppException {
        Cruise cruise = cruiseFacadeMow.findByUUID(uuid);
        checkForOptimisticLock(cruise, version);

        Account account = getCurrentUser();

        if (!cruise.isActive()) {
            throw new CruiseManagerException(CRUISE_ALREADY_BLOCKED);
        }

        if (!cruise.isPublished()) {
            throw new CruiseManagerException(CANNOT_BLOCK_THIS_CRUISE);
        }

        if (account.getAccessLevels().stream()
                .noneMatch(accessLevel -> accessLevel.getAccessLevelType() == AccessLevelType.ADMINISTRATOR) &&
                account.getAccessLevels().stream()
                        .noneMatch(accessLevel -> accessLevel.getAccessLevelType() == AccessLevelType.MODERATOR)) {

            BusinessWorker businessWorker = (BusinessWorker) AccountMapper.getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);

            if (cruise.getCruisesGroup().getCompany().getNIP() != businessWorker.getCompany().getNIP()) {
                throw new CruiseManagerException(BUSINESS_WORKER_ADD_CRUISE_TO_BAD_COMPANY);
            }
        }

        cruise.setActive(false);
        setUpdatedMetadata(cruise);
    }

    @RolesAllowed("editCruise")
    @Override
    public void editCruise(LocalDateTime startDate, LocalDateTime endDate, UUID uuid, Long version) throws BaseAppException {
        Cruise cruiseToEdit = cruiseFacadeMow.findByUUID(uuid);
        checkForOptimisticLock(cruiseToEdit, version);

        Account account = getCurrentUser();

        if (!cruiseToEdit.isActive() || cruiseToEdit.isPublished()) {
            throw new CruiseManagerException(CANNOT_EDIT_THIS_CRUISE);
        }

        BusinessWorker businessWorker = (BusinessWorker) AccountMapper.getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);
        if (cruiseToEdit.getCruisesGroup().getCompany().getNIP() != businessWorker.getCompany().getNIP()) {
            throw new CruiseManagerException(BUSINESS_WORKER_ADD_CRUISE_TO_BAD_COMPANY);
        }

        cruiseToEdit.setEndDate(endDate);
        cruiseToEdit.setStartDate(startDate);
        setUpdatedMetadata(cruiseToEdit);
    }

    @PermitAll
    @Override
    public Cruise getCruise(UUID uuid) throws BaseAppException {
        return cruiseFacadeMow.findByUUID(uuid);
    }

    @PermitAll
    @Override
    public List<Cruise> getCruisesByCruiseGroup(UUID uuid) throws BaseAppException {
        return cruiseFacadeMow.findByCruiseGroupUUID(uuid);
    }

    @RolesAllowed("publishCruise")
    @Override
    public void publishCruise(long cruiseVersion, UUID cruiseUuid) throws BaseAppException {
        Cruise cruise = cruiseFacadeMow.findByUUID(cruiseUuid);
        checkForOptimisticLock(cruise, cruiseVersion);

        BusinessWorker businessWorker = (BusinessWorker) AccountMapper.getAccessLevel(getCurrentUser(), AccessLevelType.BUSINESS_WORKER);

        if (cruise.getCruisesGroup().getCompany().getNIP() != businessWorker.getCompany().getNIP()) {
            throw new CruiseManagerException(BUSINESS_WORKER_ADD_CRUISE_TO_BAD_COMPANY);
        }

        cruise.setPublished(true);
        setUpdatedMetadata(cruise);
        cruiseFacadeMow.edit(cruise);
    }

    @PermitAll
    @Override
    public List<Cruise> getPublishedCruises() throws BaseAppException {
        return cruiseFacadeMow.getPublishedCruises();
    }

    @PermitAll
    @Override
    public Cruise findByUUID(UUID uuid) throws BaseAppException {
        return cruiseFacadeMow.findByUUID(uuid);
    }

}
