package com.cruisely.cruise.dto.cruiseGroups;

import lombok.*;
import com.cruisely.cruise.dto.cruises.CruiseAddressDto;
import com.cruisely.cruise.dto.cruises.CruisePictureDto;
import com.cruisely.cruise.dto.companies.CompanyLightDto;
import com.cruisely.validators.CompanyName;

import javax.validation.constraints.Positive;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class CruiseGroupDto {
    @CompanyName
    private CompanyLightDto company;

    private String name;

    @Positive
    private long numberOfSeats;
    @Positive
    private Double price;

    private CruiseAddressDto cruiseAddress;

    private List<CruisePictureDto> cruisePictures;
    @Positive
    private long version;

    private boolean active;
}
