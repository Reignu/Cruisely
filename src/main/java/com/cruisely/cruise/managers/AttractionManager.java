package com.cruisely.cruise.managers;

import com.cruisely.common.IntegrityUtils;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.cruise.Attraction;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.exceptions.AccountManagerException;
import com.cruisely.exceptions.AttractionManagerException;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.auth.endpoints.converters.AccountMapper;
import com.cruisely.cruise.facades.AttractionFacadeMow;
import com.cruisely.cruise.facades.CruiseFacadeMow;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;

/**
 * Class that manages business logic for attractions
 */
@Stateful
@Interceptors(TrackingInterceptor.class)
public class AttractionManager extends BaseManagerMow implements AttractionManagerLocal {

    @Inject
    private AttractionFacadeMow attractionFacadeMow;

    @Inject
    private CruiseFacadeMow cruiseFacadeMow;

    @RolesAllowed("deleteAttraction")
    @Override
    public void deleteAttraction(UUID uuid) throws BaseAppException {
        Attraction attraction = attractionFacadeMow.findByUUID(uuid);

        validateAttractionChangesPermission(attraction.getCruise(),
                ATTRACTION_DELETE_CRUISE_PUBLISHED_ERROR,
                ATTRACTION_DELETE_CRUISE_DISABLED_ERROR,
                ATTRACTION_DELETE_CRUISE_ALREADY_STARTED_ERROR
        );

        attractionFacadeMow.remove(attraction);
    }

    @RolesAllowed("addAttraction")
    @Override
    public UUID addAttraction(Attraction attraction, UUID cruiseUUID) throws BaseAppException {
        Cruise cruise = cruiseFacadeMow.findByUUID(cruiseUUID);

        validateAttractionChangesPermission(cruise,
                ATTRACTION_CREATION_CRUISE_PUBLISHED_ERROR,
                ATTRACTION_CREATION_CRUISE_DISABLED_ERROR,
                ATTRACTION_CREATION_CRUISE_ALREADY_STARTED_ERROR
        );

        attraction.setCruise(cruise);
        setCreatedMetadata(getCurrentUser(), attraction);
        attractionFacadeMow.create(attraction);
        return attraction.getUuid();
    }

    @RolesAllowed("editAttraction")
    @Override
    public void editAttraction(UUID attractionUUID, String newName, String newDescription, double newPrice, int newNumberOfSeats, long version) throws BaseAppException {
        Attraction attraction = attractionFacadeMow.findByUUID(attractionUUID);
        IntegrityUtils.checkForOptimisticLock(attraction, version);

        validateAttractionChangesPermission(attraction.getCruise(),
                ATTRACTION_EDIT_CRUISE_PUBLISHED_ERROR,
                ATTRACTION_EDIT_CRUISE_DISABLED_ERROR,
                ATTRACTION_EDIT_CRUISE_ALREADY_STARTED_ERROR
        );

        attraction.setName(newName);
        attraction.setDescription(newDescription);
        attraction.setPrice(newPrice);
        attraction.setNumberOfSeats(newNumberOfSeats);
        setUpdatedMetadata(attraction);

        attractionFacadeMow.edit(attraction);
    }


    @PermitAll
    @Override
    public List<Attraction> findByCruiseUUID(UUID uuid) throws BaseAppException {
        return attractionFacadeMow.findByCruiseUUID(uuid);
    }

    @PermitAll
    @Override
    public Attraction findByUUID(UUID uuid) throws BaseAppException {
        return attractionFacadeMow.findByUUID(uuid);
    }


    private void validateAttractionChangesPermission(Cruise cruise, String cruisePublishedError, String cruiseDisabledError, String cruiseStartedError) throws BaseAppException {
        BusinessWorker businessWorker = (BusinessWorker) AccountMapper.getAccessLevel(getCurrentUser(), AccessLevelType.BUSINESS_WORKER);

        if (!cruise.getCruisesGroup().getCompany().equals(businessWorker.getCompany())) {
            throw new AttractionManagerException(NOT_YOURS_CRUISE);
        }

        if (cruise.isPublished()) {
            throw new AccountManagerException(cruisePublishedError);
        }

        if (!cruise.isActive()) {
            throw new AccountManagerException(cruiseDisabledError);
        }

        if (cruise.getStartDate().isBefore(LocalDateTime.now())) {
            throw new AttractionManagerException(cruiseStartedError);
        }
    }
}