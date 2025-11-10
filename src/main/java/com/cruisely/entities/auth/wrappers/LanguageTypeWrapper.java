package com.cruisely.entities.auth.wrappers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.auth.LanguageType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.entities.auth.wrappers.LanguageTypeWrapper.NAME_CONSTRAINT;


@Entity(name = "language_types")
@NamedQueries({
        @NamedQuery(name = "LanguageTypeWrapper.findByName", query = "SELECT lt FROM language_types lt WHERE lt.name = :name")
})
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name", name = NAME_CONSTRAINT)
        }
)
@ToString
public class LanguageTypeWrapper {
    public static final String NAME_CONSTRAINT = "language_types_name_unique_constraint";
    @Getter
    @Id
    @GeneratedValue
    @ToString.Exclude
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    @Setter
    @Getter
    @NotNull(message = CONSTRAINT_NOT_NULL)
    private LanguageType name;

    public LanguageTypeWrapper(LanguageType name) {
        this.name = name;
    }

    public LanguageTypeWrapper() {

    }
}