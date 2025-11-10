package com.cruisely.cruise.endpoints.converters;

import com.cruisely.entities.cruise.Attraction;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.entities.cruise.CruiseGroup;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.dto.attractions.AddAttractionDto;
import com.cruisely.cruise.dto.attractions.AttractionDto;

import java.util.UUID;

public class AttractionMapper {

    private AttractionMapper() {
    }

    public static AttractionDto toAttractionDto(Attraction attraction) throws BaseAppException {
        return new AttractionDto(attraction.getUuid().toString(), attraction.getName(), attraction.getDescription(), attraction.getPrice(), attraction.getNumberOfSeats(), attraction.getVersion());
    }

    public static Attraction toAttraction(AddAttractionDto attractionDto) {
        return new Attraction(attractionDto.getName(), attractionDto.getDescription(), attractionDto.getPrice(), attractionDto.getNumberOfSeats(), null);
    }
}
