package com.cruisely.entities.cruise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;
import com.cruisely.entities.auth.Address;
import com.cruisely.validators.CompanyName;
import com.cruisely.validators.PhoneNumber;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.entities.cruise.Company.COMPANY_NAME_UNIQUE_CONSTRAINT;
import static com.cruisely.entities.cruise.Company.NIP_UNIQUE_CONSTRAINT;

@Entity(name = "companies")
@NamedQueries({
        @NamedQuery(name = "Company.findByName", query = "SELECT company FROM companies company WHERE company.name = :name"),
        @NamedQuery(name = "Company.findByNIP", query = "SELECT company FROM companies company WHERE company.NIP = :nip")

})
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nip"}, name = NIP_UNIQUE_CONSTRAINT),
        @UniqueConstraint(columnNames = {"name"}, name = COMPANY_NAME_UNIQUE_CONSTRAINT)
})
@ToString
public class Company extends BaseEntity {
    public static final String NIP_UNIQUE_CONSTRAINT = "companies_nip_unique_constraint";
    public static final String COMPANY_NAME_UNIQUE_CONSTRAINT = "companies_name_unique_constraint";

    @Getter
    @Id
    @SequenceGenerator(name = "COMPANY_SEQ_GEN", sequenceName = "companies_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "address_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Address address;

    @Getter
    @Setter
    @CompanyName
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Getter
    @Setter
    @PhoneNumber
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Getter
    @Setter
    @Min(value = 1000000000L)
    @Max(value = 9999999999L)
    @Column(name = "nip", nullable = false, unique = true)
    private long NIP;

    public Company(Address address, String name, String phoneNumber, long NIP) {
        this.address = address;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.NIP = NIP;
    }

    public Company() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company that = (Company) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public Long getIdentifier() {
        return id;
    }
}
