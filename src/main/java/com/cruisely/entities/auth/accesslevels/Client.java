package com.cruisely.entities.auth.accesslevels;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.auth.AccessLevel;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.Address;
import com.cruisely.validators.PhoneNumber;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;
import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;

@Entity(name = "clients")
@DiscriminatorValue("Client")
@ToString
public class Client extends AccessLevel {

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "home_address_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    private Address homeAddress;

    @Getter
    @Setter
    @PhoneNumber
    @Column(name = "phone_number", nullable = false)
    @NotNull(message = CONSTRAINT_NOT_EMPTY)
    private String phoneNumber;

    @Override
    public AccessLevelType getAccessLevelType() {
        return AccessLevelType.CLIENT;
    }

    public Client() {
        this.enabled = true;
    }

    public Client(Address homeAddress, String phoneNumber) {
        this.homeAddress = homeAddress;
        this.phoneNumber = phoneNumber;
        this.enabled = true;
    }

    public Client(Address homeAddress, String phoneNumber, boolean enabled) {
        this.homeAddress = homeAddress;
        this.phoneNumber = phoneNumber;
        this.enabled = enabled;
    }

    public Client(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.enabled = true;
    }
}
