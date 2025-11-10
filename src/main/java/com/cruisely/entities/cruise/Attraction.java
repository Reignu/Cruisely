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

import java.util.UUID;

import static com.cruisely.common.I18n.*;

@Entity(name = "attractions")
@NamedQueries({
        @NamedQuery(name = "Attraction.findByName", query = "SELECT att FROM attractions att WHERE att.name = :name"),
        @NamedQuery(name = "Attraction.findByIdIfReserved", query = "SELECT att FROM attractions att WHERE att.name = :name"),
        @NamedQuery(name = "Attraction.findByCruiseUUID", query = "SELECT att FROM attractions att WHERE att.cruise.uuid = :uuid"),
        @NamedQuery(name = "Attraction.countReservedSpots", query = "SELECT SUM(r.numberOfSeats) FROM reservations r WHERE :attraction MEMBER OF r.attractions"),
        @NamedQuery(name = "Attraction.findByUUID", query = "SELECT att FROM attractions att WHERE att.uuid = :uuid")
})
@ToString
public class Attraction extends BaseEntity {

    @Getter
    @Id
    @SequenceGenerator(name = "ATTRACTION_SEQ_GEN", sequenceName = "attractions_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ATTRACTION_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Getter
    @Setter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "description")
    private String description;

    @Getter
    @Setter
    @Positive(message = CONSTRAINT_POSITIVE)
    @Column(name = "price")
    private double price;

    @Getter
    @Setter
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    @Column(name = "number_of_seats")
    private long numberOfSeats;

    @Getter
    @Setter
    @Column(name = "has_free_spots")
    private boolean hasFreeSpots;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "cruise_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Cruise cruise;

    public Attraction(String name, String description,
                      double price, long numberOfSeats, boolean hasFreeSpots, Cruise cruise) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.numberOfSeats = numberOfSeats;
        this.hasFreeSpots = hasFreeSpots;
        this.cruise = cruise;
    }

    public Attraction(String name, String description, double price, long numberOfSeats, Cruise cruise) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.numberOfSeats = numberOfSeats;
        this.cruise = cruise;
        this.hasFreeSpots = true;
    }

    public Attraction() {
    }

    @Override
    public Long getIdentifier() {
        return id;
    }
}

