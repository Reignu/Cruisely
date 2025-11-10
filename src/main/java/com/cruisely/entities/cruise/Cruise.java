package com.cruisely.entities.cruise;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.entities.cruise.Cruise.UUID_CONSTRAINT;

@Entity(name = "cruises")
@NamedQueries({
        @NamedQuery(name = "Cruise.findByUUID", query = "SELECT c FROM cruises c WHERE c.uuid = :uuid"),
        // IntelliJ shows an error, but it's caused - as far as I know - by the insufficient IDE's support for Postgres.
        // And it works
        @NamedQuery(name = "Cruise.findAllPublished", query = "SELECT c FROM cruises c WHERE c.published = true AND c.active = true AND c.startDate >= NOW()"),
        @NamedQuery(name = "Cruise.findByCruiseGroupUUID", query = "SELECT c FROM cruises c WHERE c.cruisesGroup.uuid = :uuid"),
})
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "uuid", name = UUID_CONSTRAINT),

        }
)
@ToString
public class Cruise extends BaseEntity {

    public static final String UUID_CONSTRAINT = "cruises_uuid_unique_constraint";

    @Getter
    @Id
    @SequenceGenerator(name = "CRUISE_SEQ_GEN", sequenceName = "cruises_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRUISE_SEQ_GEN")
    @ToString.Exclude
    private long id;


    @Getter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Getter
    @Setter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Getter
    @Setter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Getter
    @Setter
    @Column(name = "active")
    private boolean active;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "cruises_group_id")
    @Valid
    @ToString.Exclude
    private CruiseGroup cruisesGroup;

    @Getter
    @Setter
    @Column(name = "published")
    private boolean published;

    @Getter
    @Setter
    @ToString.Exclude
    @OneToMany(mappedBy = "cruise", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Attraction> attractions = new ArrayList<>();

    @Getter
    @Setter
    @ToString.Exclude
    @OneToMany(mappedBy = "cruise", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Reservation> reservations;


    public Cruise(LocalDateTime startDate, LocalDateTime endDate,
                  CruiseGroup cruisesGroup) {
        this.uuid = UUID.randomUUID();
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
        this.cruisesGroup = cruisesGroup;
        this.published = false;
    }

    public Cruise(LocalDateTime startDate, LocalDateTime endDate, boolean active,
                  CruiseGroup cruisesGroup) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.cruisesGroup = cruisesGroup;
        this.uuid = UUID.randomUUID();
    }

    public Cruise() {
    }

    @Override
    public Long getIdentifier() {
        return id;
    }

}

