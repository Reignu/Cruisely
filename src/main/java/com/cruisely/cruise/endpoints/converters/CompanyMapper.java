package com.cruisely.cruise.endpoints.converters;

import com.cruisely.entities.auth.Address;
import com.cruisely.entities.cruise.Company;
import com.cruisely.auth.dto.AddressDto;
import com.cruisely.cruise.dto.companies.AddCompanyDto;
import com.cruisely.cruise.dto.companies.CompanyLightDto;
import com.cruisely.cruise.dto.companies.CompanyDto;

public class CompanyMapper {
    private CompanyMapper() {
    }

    public static CompanyLightDto mapCompanyToCompanyLightDto(Company company) {
        return new CompanyLightDto(company.getName(), company.getNIP());
    }

    public static CompanyDto mapCompanyToCompanyDto(Company company) {
        return new CompanyDto(company.getName(), company.getPhoneNumber(), company.getNIP(), company.getAddress().getHouseNumber(),
                company.getAddress().getStreet(), company.getAddress().getPostalCode(), company.getAddress().getCity(), company.getAddress().getCountry());
    }


    /**
    * Maps an AddCompanyDto object to a Company object
    *
    * @param company object of class AddCompanyDto
    * @return Company object
     */
    public static Company mapAddCompanyDtoToCompany(AddCompanyDto company) {
        return new Company(
                mapAddressDtoToAddress(company.getAddressDto()),
                company.getName(),
                company.getPhoneNumber(),
                company.getNip());
    }

    /**
    * Maps an AddressDto object to an Address object
    *
    * @param address object of class AddressDto
    * @return Address object
     */
    public static Address mapAddressDtoToAddress(AddressDto address) {
        return new Address(
                address.getHouseNumber(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity(),
                address.getCountry());
    }
}
