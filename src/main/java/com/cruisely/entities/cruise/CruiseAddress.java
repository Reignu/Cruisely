package com.cruisely.entities.cruise;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;
import com.cruisely.validators.City;
import com.cruisely.validators.Country;
import com.cruisely.validators.Street;
import com.cruisely.validators.StreetNumber;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;
import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE;


@Entity(name = "cruise_addresses")
@ToString
public class CruiseAddress extends BaseEntity {

    @Getter
    @Id
    @SequenceGenerator(name = "CRUISE_ADDRESS_SEQ_GEN", sequenceName = "cruise_addresses_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRUISE_ADDRESS_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @Setter
    @Street
    @Column(name = "street")
    private String street;

    @Getter
    @Setter
    @StreetNumber
    @Column(name = "street_number")
    private String streetNumber;

    @Getter
    @Setter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "harbor_name")
    private String harborName;

    @Getter
    @Setter
    @City
    @Column(name = "city_name")
    private String cityName;

    @Getter
    @Setter
    @Country
    @Column(name = "country_name")
    private String countryName;

    public CruiseAddress(String street, String streetNumber, String harborName, String cityName, String countryName) {
        this.street = street;
        this.streetNumber = streetNumber;
        this.harborName = harborName;
        this.cityName = cityName;
        this.countryName = countryName;
    }

    public CruiseAddress() {
    }

    @Override
    public Long getIdentifier() {
        return id;
    }
}

