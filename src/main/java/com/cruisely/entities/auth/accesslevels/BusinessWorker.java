package com.cruisely.entities.auth.accesslevels;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.auth.AccessLevel;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.cruise.Company;
import com.cruisely.validators.PhoneNumber;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;
import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;

@Entity(name = "business_workers")
@NamedQueries({
        @NamedQuery(name = "Company.findBusinessWorkersByCompanyName", query = "SELECT bw FROM business_workers bw where bw.company.name = :companyName"),
        @NamedQuery(name = "BusinessWorker.findALlUnconfirmed", query = "SELECT acc FROM business_workers acc WHERE acc.confirmed = false ")
})

@DiscriminatorValue("BusinessWorker")
@ToString
public class BusinessWorker extends AccessLevel {

    @Getter
    @Setter
    @PhoneNumber
    @Column(name = "phone_number", nullable = false)
    @NotNull(message = CONSTRAINT_NOT_EMPTY)
    private String phoneNumber;

    @Getter
    @Setter
    @Column(name = "confirmed", nullable = false)
    private boolean confirmed;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(updatable = false, nullable = false, name = "company_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Company company;

    @Override
    public AccessLevelType getAccessLevelType() {
        return AccessLevelType.BUSINESS_WORKER;
    }

    public BusinessWorker() {
    }

    public BusinessWorker(String phoneNumber, boolean enabled) {
        this.phoneNumber = phoneNumber;
        this.enabled = enabled;
        this.confirmed = false;
    }
}