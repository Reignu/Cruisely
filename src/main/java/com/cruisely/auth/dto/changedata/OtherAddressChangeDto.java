package com.cruisely.auth.dto.changedata;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.validators.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtherAddressChangeDto {
    @HouseNumber
    private String newHouseNumber;
    @Street
    private String newStreet;
    @PostCode
    private String newPostalCode;
    @City
    private String newCity;
    @Country
    private String newCountry;
}