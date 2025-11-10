package com.cruisely.entities.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.common.IdentifiableEntity;
import com.cruisely.entities.common.wrappers.AlterTypeWrapper;
import com.cruisely.entities.auth.Account;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE_OR_ZERO;

@MappedSuperclass
@ToString
@Getter
@Setter
public abstract class BaseEntity implements IdentifiableEntity {
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "creation_date_time", updatable = false)
    private LocalDateTime creationDateTime;

    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Column(name = "last_alter_date_time")
    private LocalDateTime lastAlterDateTime;

    @OneToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "created_by_id", updatable = false)
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Account createdBy;

    @OneToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "altered_by_id")
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private Account alteredBy;

    @JoinColumn(name = "alter_type_id")
    @OneToOne(cascade = {CascadeType.PERSIST})
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private AlterTypeWrapper alterType;

    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    @Version
    @Column(nullable = false)
    private long version;

    @PrePersist
    private void prePersist() {
        creationDateTime = LocalDateTime.now();
        lastAlterDateTime = creationDateTime; //referencing creationDateTime as LDT is immutable
    }

    @PreUpdate
    public void preUpdate() {
        this.setLastAlterDateTime(LocalDateTime.now());
    }

}
