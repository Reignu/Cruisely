package com.cruisely.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.validators.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    @HouseNumber
    private String houseNumber;

    @Street
    private String street;

    @PostCode
    private String postalCode;

    @City
    private String city;

    @Country
    private String country;
}
