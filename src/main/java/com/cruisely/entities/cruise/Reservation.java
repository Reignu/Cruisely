package com.cruisely.entities.cruise;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;
import com.cruisely.entities.auth.accesslevels.Client;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;

@NamedQueries({
        @NamedQuery(name = "Reservation.findByUUID", query = "SELECT reservation FROM reservations reservation WHERE reservation.uuid = :uuid"),
        @NamedQuery(name = "Reservation.findCruiseReservations", query = "SELECT reservation FROM reservations reservation WHERE reservation.cruise.uuid = :uuid"),
        @NamedQuery(name = "Reservation.findByUUIDAndLogin", query = "SELECT res FROM reservations res WHERE res.uuid=:uuid AND res.client.account.login=:login"),
        @NamedQuery(name = "Reservation.findByLogin", query = "SELECT res FROM reservations res WHERE res.client.account.login=:login")
})
@Entity(name = "reservations")
@ToString
public class Reservation extends BaseEntity {

    @Getter
    @Id
    @SequenceGenerator(name = "RESERVATION_SEQ_GEN", sequenceName = "reservations_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESERVATION_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Basic
    @Getter
    @Setter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Getter
    @Setter
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    @Column(name = "number_of_seats")
    private long numberOfSeats;

    @Getter
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "cruise_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Cruise cruise;

    @Getter
    @Setter
    @Positive(message = CONSTRAINT_POSITIVE)
    @Column(name = "price")
    private Double price;

    @Getter
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "client_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Client client;

    @Getter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable(name = "reservation_attractions",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "attraction_id")
    )
    @ToString.Exclude
    private final List<Attraction> attractions = new ArrayList<>();

    public Reservation(long numberOfSeats, Cruise cruise, Client client) {
        this.numberOfSeats = numberOfSeats;
        this.cruise = cruise;
        this.client = client;
        this.uuid = UUID.randomUUID();
    }

    public Reservation(long numberOfSeats, Cruise cruise, Double price, Client client) {
        this.numberOfSeats = numberOfSeats;
        this.cruise = cruise;
        this.price = price;
        this.client = client;
        this.uuid = UUID.randomUUID();
    }

    public Reservation() {
    }

    @Override
    public Long getIdentifier() {
        return id;
    }
}
