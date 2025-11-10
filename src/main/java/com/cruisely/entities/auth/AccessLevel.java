package com.cruisely.entities.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;

@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "access_level", discriminatorType = DiscriminatorType.STRING)
@Entity(name = "access_levels")
@ToString
public abstract class AccessLevel extends BaseEntity {
    @Getter
    @Id
    @SequenceGenerator(name = "ACCESS_LEVEL_SEQ_GEN", sequenceName = "access_level_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCESS_LEVEL_SEQ_GEN")
    @Column(name = "id")
    @ToString.Exclude
    private long id;

    @Getter
    @Setter
    protected boolean enabled;

    @JoinColumn(name = "account_id", referencedColumnName = "id", updatable = false)
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @Getter
    @Setter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Account account;

    public abstract AccessLevelType getAccessLevelType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessLevel that = (AccessLevel) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        //using Class as HashCode to avoid any possible duplicate same access level associated with account
        return this.getClass().hashCode();
    }

    @Override
    public Long getIdentifier() {
        return id;
    }

}