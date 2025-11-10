package com.cruisely.cruise.dto.cruises;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CruiseGroupWithCruisesDto {

    private UUID uuid;
    private String name;
    private Double price;
    private String img;
    List<ShortCruiseDto> relatedCruises;
}
