package com.cruisely.cruise.endpoints;


import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.BaseEndpoint;
import com.cruisely.common.mappers.MetadataMapper;
import com.cruisely.entities.cruise.*;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.cruise.dto.cruiseGroups.CruiseGroupWithDetailsDto;
import com.cruisely.cruise.dto.cruiseGroups.AddCruiseGroupDto;
import com.cruisely.cruise.dto.cruiseGroups.ChangeCruiseGroupDto;
import com.cruisely.cruise.endpoints.converters.CruiseGroupMapper;
import com.cruisely.cruise.managers.CruiseGroupManagerLocal;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class responsible for collecting mapped DTO objects into model objects related to cruise groups
 * and invoking business logic methods with the mapped objects.
 */
@Stateful
@Interceptors(TrackingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CruiseGroupEndpoint extends BaseEndpoint implements CruiseGroupEndpointLocal {

    @Inject
    private CruiseGroupManagerLocal cruiseGroupManager;

    @Override
    @RolesAllowed("addCruiseGroup")
    public void addCruiseGroup(AddCruiseGroupDto addCruiseGroupDto) throws BaseAppException {
        CruiseAddress cruiseAddress = CruiseGroupMapper.extractAddressForAddingCruiseGroup(addCruiseGroupDto);
        List<CruisePicture> cruisePicture = CruiseGroupMapper.extractCruiseGroupPicturesFromAddingCruiseGroup(addCruiseGroupDto);
        this.cruiseGroupManager.addCruiseGroup(addCruiseGroupDto.getCompanyName(), addCruiseGroupDto.getName(), addCruiseGroupDto.getNumberOfSeats(),
                addCruiseGroupDto.getPrice(), cruiseAddress, cruisePicture,addCruiseGroupDto.getDescription());
    }

    @Override
    @RolesAllowed("changeCruiseGroup")
    public void changeCruiseGroup(ChangeCruiseGroupDto changeCruiseGroupDto) throws BaseAppException {
        CruiseAddress start_address = new CruiseAddress(changeCruiseGroupDto.getCruiseAddress().getStreet(), changeCruiseGroupDto.getCruiseAddress().getStreetNumber(),
                changeCruiseGroupDto.getCruiseAddress().getHarborName(), changeCruiseGroupDto.getCruiseAddress().getCityName(),
                changeCruiseGroupDto.getCruiseAddress().getCountryName());
                if(changeCruiseGroupDto.getPicture() != null){
                    CruisePicture picture = new CruisePicture( changeCruiseGroupDto.getPicture().getDataURL(),"zdjecie");
                    cruiseGroupManager.changeCruiseGroup(changeCruiseGroupDto.getName(), changeCruiseGroupDto.getNumberOfSeats()
                            , changeCruiseGroupDto.getPrice(), start_address, changeCruiseGroupDto.getVersion(),changeCruiseGroupDto.getDescription(),
                            picture,changeCruiseGroupDto.getUuid());
                }else
                {
                    CruisePicture picture = new CruisePicture("","");
                    cruiseGroupManager.changeCruiseGroup(changeCruiseGroupDto.getName(), changeCruiseGroupDto.getNumberOfSeats()
                            , changeCruiseGroupDto.getPrice(), start_address, changeCruiseGroupDto.getVersion(),changeCruiseGroupDto.getDescription(), picture,changeCruiseGroupDto.getUuid());
                }


    }


    @Override
    @RolesAllowed("getAllCruiseGroupList")
    public List<CruiseGroupWithDetailsDto> getCruiseGroupsInfo() throws FacadeException, BaseAppException {
        List<CruiseGroupWithDetailsDto> res = new ArrayList<>();
        for (CruiseGroup cruiseGroup : cruiseGroupManager.getAllCruiseGroups()) {
            List<Cruise> cruise = cruiseGroupManager.getCruiseBelongsToCruiseGroup(cruiseGroup);
            res.add(CruiseGroupMapper.toCruiseGroupWithDetailsDto(cruiseGroup,cruise));
        }
        return res;
    }

    @RolesAllowed("deactivateCruiseGroup")
    @Override
    public void deactivateCruiseGroup(UUID uuid, Long version) throws BaseAppException {
        this.cruiseGroupManager.deactivateCruiseGroup(uuid, version);
    }
    @RolesAllowed("getCruiseGroupForBusinessWorker")
    @Override
    public List<CruiseGroupWithDetailsDto> getCruiseGroupForBusinessWorker(String companyName) throws BaseAppException {
        List<CruiseGroupWithDetailsDto> res = new ArrayList<>();
        for (CruiseGroup cruiseGroup : cruiseGroupManager.getCruiseGroupForBusinessWorker(companyName)) {
            List<Cruise> cruise = cruiseGroupManager.getCruiseBelongsToCruiseGroup(cruiseGroup);
            res.add(CruiseGroupMapper.toCruiseGroupWithDetailsDto(cruiseGroup,cruise));
        }
      return res;
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public MetadataDto getCruiseGroupMetadata(UUID uuid) throws BaseAppException {
        return MetadataMapper.toMetadataDto(cruiseGroupManager.findByUUID(uuid));
    }
}
