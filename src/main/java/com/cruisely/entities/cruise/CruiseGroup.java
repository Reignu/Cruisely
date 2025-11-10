package com.cruisely.entities.cruise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;

@Entity(name = "cruises_groups")
@NamedQueries({
        @NamedQuery(name = "CruiseGroup.findByUUID", query = "SELECT crg FROM cruises_groups crg WHERE crg.uuid = :uuid"),
        @NamedQuery(name = "CruiseGroup.findByName", query = "SELECT crg FROM cruises_groups crg WHERE crg.name = :name"),
        @NamedQuery(name= "CruiseGroup.findCruises", query = "SELECT cr FROM cruises  cr WHERE cr.cruisesGroup =:name"),
        @NamedQuery(name = "CruiseGroup.findForBusinessWorker", query = "SELECT crg FROM cruises_groups crg WHERE crg.company = :name"),

})
@ToString
public class CruiseGroup extends BaseEntity {

    @Getter
    @Id
    @SequenceGenerator(name = "CRUISES_GROUP_SEQ_GEN", sequenceName = "cruises_groups_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRUISES_GROUP_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "company_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Company company;

    @Getter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Getter
    @Setter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "description")
    private String description;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "start_address_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private CruiseAddress address;

    @Getter
    @Setter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    @Column(name = "number_of_seats")
    private long numberOfSeats;

    @Getter
    @Setter
    @Positive(message = CONSTRAINT_POSITIVE)
    @Column(name = "price")
    private Double price;

    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "cruises_group_pictures",
            joinColumns = @JoinColumn(name = "cruises_group_id"),
            inverseJoinColumns =  @JoinColumn(name = "cruise_picture_id")

    )
    @Valid
  //  @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @ToString.Exclude
    private  List<CruisePicture> cruisePictures = new ArrayList<>();

    @Getter
    @Setter
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    @Column(name = "average_rating")
    private Double averageRating;



    @Getter
    @Setter
    @NotNull
    @Column(name = "active")
    private boolean active;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "cruiseGroup")
    private List<Rating> ratings;

    public CruiseGroup(Company company, CruiseAddress address, String name, long numberOfSeats,
                       Double price,  List<CruisePicture> cruisePictures,String description) {
        this.company = company;
        this.address = address;
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        this.price = price;
        this.averageRating = 0.0;
        this.cruisePictures = cruisePictures;
        this.description = description;
        this.uuid= uuid.randomUUID();
        this.active=true;
    }

    public CruiseGroup() {
    }

    @Override
    public Long getIdentifier() {
        return id;
    }


}


