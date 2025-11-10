package com.cruisely.cruise.dto.cruises;

import lombok.*;
import com.cruisely.validators.*;

import javax.validation.constraints.Positive;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CruiseAddressDto {
    @Street
    private String street;

    @StreetNumber
    private String streetNumber;

    @HarborName
    private String harborName;

    @City
    private String cityName;

    @Country
    private String countryName;
}
