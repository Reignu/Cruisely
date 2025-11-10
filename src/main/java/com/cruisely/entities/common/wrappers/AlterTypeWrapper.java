package com.cruisely.entities.common.wrappers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.AlterType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.entities.common.wrappers.AlterTypeWrapper.NAME_CONSTRAINT;

@Entity(name = "alter_types")
@NamedQueries({
        @NamedQuery(name = "AlterTypeWrapper.findByName", query = "SELECT at FROM alter_types at WHERE at.name = :name")
})
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name", name = NAME_CONSTRAINT),
        }
)
@ToString
public class AlterTypeWrapper {
    public static final String NAME_CONSTRAINT = "alter_types_name_unique_constraint";
    @Getter
    @Id
    @GeneratedValue
    @ToString.Exclude
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    @Getter
    @Setter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    private AlterType name;

    public AlterTypeWrapper(AlterType name) {
        this.name = name;
    }

    public AlterTypeWrapper() {

    }
}
