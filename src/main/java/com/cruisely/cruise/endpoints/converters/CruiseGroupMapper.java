package com.cruisely.cruise.endpoints.converters;

import com.cruisely.entities.cruise.Cruise;
import com.cruisely.entities.cruise.CruiseAddress;
import com.cruisely.entities.cruise.CruiseGroup;
import com.cruisely.entities.cruise.CruisePicture;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.dto.cruiseGroups.CruiseGroupWithDetailsDto;
import com.cruisely.cruise.dto.companies.CompanyLightDto;
import com.cruisely.cruise.dto.cruiseGroups.*;
import com.cruisely.cruise.dto.cruises.CruiseAddressDto;
import com.cruisely.cruise.dto.cruises.CruiseForCruiseGroupDto;
import com.cruisely.cruise.dto.cruises.CruisePictureDto;
import com.cruisely.cruise.dto.ratings.RatingDto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class responsible for mapping DTO objects to model objects
 */
public class CruiseGroupMapper {
    private CruiseGroupMapper() {
    }

    /**
     * Maps a DTO object to a CruiseAddress object
     *
     * @param addCruiseGroup DTO object
     * @return model object representing the cruise address
     */
    public static CruiseAddress extractAddressForAddingCruiseGroup(AddCruiseGroupDto addCruiseGroup) {
        return new CruiseAddress(addCruiseGroup.getCruiseAddress().getStreet(), addCruiseGroup.getCruiseAddress().getStreetNumber(),
                addCruiseGroup.getCruiseAddress().getHarborName(), addCruiseGroup.getCruiseAddress().getCityName(),
                addCruiseGroup.getCruiseAddress().getCountryName());
    }

    /**
     * Maps a DTO object to a list of CruisePicture objects
     *
     * @param addCruiseGroup DTO object
     * @return list of CruisePicture objects
     */
    public static List<CruisePicture> extractCruiseGroupPicturesFromAddingCruiseGroup(AddCruiseGroupDto addCruiseGroup) {
        List<CruisePicture> pictures = new ArrayList<>();
        if (addCruiseGroup.getCruisePictures().size() > 0) {

            for (CruisePictureDto dto : addCruiseGroup.getCruisePictures()
            ) {
                pictures.add(new CruisePicture(dto.getDataURL(), "zdjecie"));
            }
        }
        return pictures;
    }


    /**
     * Maps a CruiseAddress object to a CruiseAddress DTO
     *
     * @param cruiseAddress CruiseAddress object provided to the conversion
     * @return DTO object
     */
    public static CruiseAddressDto toCruiseAddressDto(CruiseAddress cruiseAddress) {
        return new CruiseAddressDto(cruiseAddress.getStreet(), cruiseAddress.getStreetNumber(), cruiseAddress.getHarborName(),
                cruiseAddress.getCityName(), cruiseAddress.getCountryName()
        );
    }

    /**
     * Maps a CruisePicture object to a CruisePicture DTO
     *
     * @param cruisePicture CruisePicture object provided to the conversion
     * @return DTO object
     */
    public static CruisePictureDto toCruisePictureDto(CruisePicture cruisePicture) {
        return new CruisePictureDto(cruisePicture.getImg(), cruisePicture.getImgName(), cruisePicture.getVersion());
    }

    public static List<CruiseForCruiseGroupDto> toCruiseForCruiseGroupDtos(List<Cruise> cruises) throws BaseAppException {
        List<CruiseForCruiseGroupDto> cruisesForCruiseGroup = new ArrayList<>();

        for (Cruise cruise : cruises) {
            cruisesForCruiseGroup.add(new CruiseForCruiseGroupDto(cruise.getUuid(), cruise.getStartDate(), cruise.getEndDate(), cruise.isActive(), cruise.isPublished(), cruise.getVersion()));
        }

        return cruisesForCruiseGroup;
    }

    /**
     * Maps a CruiseGroup object to a CruiseGroup DTO
     *
     * @param cruiseGroup cruise group provided for conversion
     * @return DTO object
     */
    public static CruiseGroupDto toCruiseGroupDto(CruiseGroup cruiseGroup) {
        CompanyLightDto company = CompanyMapper.mapCompanyToCompanyLightDto(cruiseGroup.getCompany());
        CruiseAddressDto address = CruiseGroupMapper.toCruiseAddressDto(cruiseGroup.getAddress());

        return new CruiseGroupDto(company, cruiseGroup.getName(), cruiseGroup.getNumberOfSeats(), cruiseGroup.getPrice(), address,
                cruiseGroup.getCruisePictures().stream().map(CruiseGroupMapper::toCruisePictureDto).collect(Collectors.toList()),
                cruiseGroup.getVersion(), cruiseGroup.isActive());
    }

    public static CruiseGroupWithUUIDDto toCruiseGroupWithUUIDDto(CruiseGroup cruiseGroup) {
        CompanyLightDto company = CompanyMapper.mapCompanyToCompanyLightDto(cruiseGroup.getCompany());
        CruiseAddressDto address = CruiseGroupMapper.toCruiseAddressDto(cruiseGroup.getAddress());
        List<RatingDto> ratings = cruiseGroup.getRatings().stream().map(RatingMapper::toRatingDto).collect(Collectors.toList());

        return new CruiseGroupWithUUIDDto(cruiseGroup.getUuid(), cruiseGroup.getDescription(), cruiseGroup.getAverageRating(), ratings, company, cruiseGroup.getName(), cruiseGroup.getNumberOfSeats(), cruiseGroup.getPrice(), address,
                cruiseGroup.getCruisePictures().stream().map(CruiseGroupMapper::toCruisePictureDto).collect(Collectors.toList()),
                cruiseGroup.getVersion(), cruiseGroup.isActive());
    }

    public static CruiseGroupWithDetailsDto toCruiseGroupWithDetailsDto(CruiseGroup cruiseGroup, List<Cruise> cruies) throws BaseAppException {
        CompanyLightDto company = CompanyMapper.mapCompanyToCompanyLightDto(cruiseGroup.getCompany());
        CruiseAddressDto address = CruiseGroupMapper.toCruiseAddressDto(cruiseGroup.getAddress());
        List<CruiseForCruiseGroupDto> cruises = CruiseGroupMapper.toCruiseForCruiseGroupDtos(cruies);
        if (cruies.size() > 0) {
            return new CruiseGroupWithDetailsDto(cruiseGroup.getUuid(), company, cruiseGroup.getName(), cruiseGroup.getNumberOfSeats(), cruiseGroup.getPrice(), address,
                    cruiseGroup.getCruisePictures().stream().map(CruiseGroupMapper::toCruisePictureDto).collect(Collectors.toList()),
                    cruiseGroup.getVersion(), cruiseGroup.getDescription(), cruiseGroup.isActive(), cruises, cruies.get(0).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE), cruies.get(0).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else
            return new CruiseGroupWithDetailsDto(cruiseGroup.getUuid(), company, cruiseGroup.getName(), cruiseGroup.getNumberOfSeats(), cruiseGroup.getPrice(), address,
                    cruiseGroup.getCruisePictures().stream().map(CruiseGroupMapper::toCruisePictureDto).collect(Collectors.toList()),
                    cruiseGroup.getVersion(), cruiseGroup.getDescription(), cruiseGroup.isActive(), cruises, "", "");
    }
}
