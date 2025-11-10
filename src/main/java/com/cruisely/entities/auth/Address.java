package com.cruisely.entities.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;
import com.cruisely.validators.*;

import javax.persistence.*;

@Entity(name = "addresses")
public class Address extends BaseEntity {
    @Getter
    @Id
    @SequenceGenerator(name = "ADDRESS_SEQ_GEN", sequenceName = "address_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDRESS_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @Setter
    @Column(name = "house_number")
    @HouseNumber
    private String houseNumber;

    @Getter
    @Setter
    @Street
    private String street;

    @Getter
    @Setter
    @PostCode
    @Column(name = "postal_code")
    private String postalCode;

    @Getter
    @Setter
    @City
    private String city;

    @Getter
    @Setter
    @Country
    private String country;

    public Address() {
    }

    public Address(String houseNumber, String street, String postalCode, String city, String country) {
        this.houseNumber = houseNumber;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }

    @Override
    public Long getIdentifier() {
        return id;
    }
}
