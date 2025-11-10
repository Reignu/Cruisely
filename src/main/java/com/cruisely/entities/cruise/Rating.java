package com.cruisely.entities.cruise;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;
import com.cruisely.entities.auth.Account;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.util.UUID;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.common.I18n.RATING_CONSTRAINT_ERROR;

@Entity(name = "ratings")
@NamedQueries({
        @NamedQuery(name = "Rating.findByCruiseGroupUUID", query = "SELECT r FROM ratings r WHERE r.cruiseGroup.uuid =:uuid"),
        @NamedQuery(name = "Rating.findByCruiseGroupUUIDAndAccountLogin", query = "SELECT r FROM ratings r WHERE r.cruiseGroup.uuid=:uuid AND r.account.login=:login"),
        @NamedQuery(name = "Rating.findByUuidAndAccountLogin", query = "SELECT r FROM ratings r WHERE r.cruiseGroup.uuid =:uuid AND r.account.login=:login"),
        @NamedQuery(name = "Rating.findUserRatings", query = "SELECT r FROM ratings r WHERE r.account.login=:login"),
        @NamedQuery(name = "Rating.countByCruiseGroupUUIDAndAccountLogin", query = "SELECT COUNT(r) FROM ratings r WHERE r.account.login=:login AND cruiseGroup.uuid=:uuid"),
        @NamedQuery(name = "Rating.findByUuid", query = "SELECT r FROM ratings r WHERE r.uuid=:uuid"),
})
@ToString
public class Rating extends BaseEntity {

    @Getter
    @Id
    @SequenceGenerator(name = "RATING_SEQ_GEN", sequenceName = "ratings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RATING_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @OneToOne
    @JoinColumn(name = "account_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Account account;

    @Getter
    @ManyToOne
    @JoinColumn(name = "cruise_group_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private CruiseGroup cruiseGroup;

    @Getter
    @Setter
    @Min(value = 1, message = RATING_CONSTRAINT_ERROR)
    @Max(value = 5, message = RATING_CONSTRAINT_ERROR)
    @Column(name = "rating")
    private Double rating;

    @Getter
    @Setter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    public Rating(@NotNull(message = CONSTRAINT_NOT_NULL) Account account,
                  @NotNull(message = CONSTRAINT_NOT_NULL) CruiseGroup cruiseGroup,
                  @NotNull(message = CONSTRAINT_NOT_NULL) Double rating) {
        this.account = account;
        this.cruiseGroup = cruiseGroup;
        this.rating = rating;
        this.uuid = UUID.randomUUID();
    }

    public Rating() {
    }

    @Override
    public Long getIdentifier() {
        return id;
    }
}
