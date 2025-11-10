package com.cruisely.cruise.endpoints.converters;


import com.cruisely.entities.cruise.Cruise;
import com.cruisely.entities.cruise.CruiseGroup;
import com.cruisely.entities.cruise.Reservation;
import com.cruisely.cruise.dto.cruiseGroups.CruiseGroupWithUUIDDto;
import com.cruisely.cruise.dto.cruises.*;
import com.cruisely.exceptions.MapperException;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.cruisely.common.I18n.CRUISE_MAPPER_DATE_PARSE;


/**
 * Class responsible for mapping DTO objects to model objects
 */
public class CruiseMapper {
    private CruiseMapper() {
    }


    /**
     * Maps a NewCruiseDto object to a Cruise object
     *
     * @param newCruiseDto DTO object representing the cruise
     * @return mapped Cruise object
     */
    public static Cruise mapNewCruiseDtoToCruise(NewCruiseDto newCruiseDto) throws MapperException {
        try {
            return new Cruise(LocalDateTime.ofInstant(Instant.parse(newCruiseDto.getStartDate()), ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(Instant.parse(newCruiseDto.getEndDate()), ZoneId.systemDefault()),null);
        } catch (DateTimeParseException e) {
            throw new MapperException(CRUISE_MAPPER_DATE_PARSE);
        }
    }

    public static CruiseDto mapCruiseToCruiseDto(Cruise cruise) {
        CruiseGroupWithUUIDDto cruiseGroupDto = CruiseGroupMapper.toCruiseGroupWithUUIDDto(cruise.getCruisesGroup());
        return new CruiseDto(cruise.getUuid().toString(), cruise.getVersion(),
                cruise.getStartDate(), cruise.getEndDate(),
                cruise.isActive(), cruiseGroupDto, cruiseGroupDto.getNumberOfSeats() - cruise.getReservations().stream().mapToLong(Reservation::getNumberOfSeats).sum());


    }

    public static RelatedCruiseDto toRelatedCruiseDto(Cruise cruise) {
        return new RelatedCruiseDto(cruise.getUuid(), cruise.getStartDate(), cruise.getEndDate(), cruise.isActive());
    }

    /**
    * Method mapping a list of cruises to a list of CruiseGroupWithCruisesDto
    * @param cruises list of cruises
    * @return List of cruise groups in the form of CruiseGroupWithCruisesDto
     */
    public static List<CruiseGroupWithCruisesDto> toListOfCruiseGroupsWithCruisesDto(List<Cruise> cruises) {
        List<CruiseGroup> cruiseGroups = new ArrayList<>();
        List<CruiseGroupWithCruisesDto> result = new ArrayList<>();

        for (int i = 0; i < cruises.size(); i++) {
            CruiseGroup cg = cruises.get(i).getCruisesGroup();
            if (!cruiseGroups.contains(cg)) {
                cruiseGroups.add(cg);
                String img = null;
                if (cg.getCruisePictures().size() > 0) {

                    img = cg.getCruisePictures().get(0).getImg();
                }
                CruiseGroupWithCruisesDto c = new CruiseGroupWithCruisesDto(cg.getUuid(), cg.getName(), cg.getPrice(), img, new ArrayList<>());
                for (int j = i; j < cruises.size(); j++) {
                    if (cruises.get(j).getCruisesGroup().getUuid() == c.getUuid()) {
                        c.getRelatedCruises().add(toShortCruiseDto(cruises.get(j)));
                    }
                }
                result.add(c);
            }
        }

        return result;
    }

    private static ShortCruiseDto toShortCruiseDto(Cruise cruise) {
        return new ShortCruiseDto(cruise.getUuid(), cruise.getStartDate(), cruise.getEndDate());
    }
}
