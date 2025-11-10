package com.cruisely.entities.common.wrappers;

import lombok.*;
import com.cruisely.entities.auth.Account;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;
import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.entities.common.wrappers.CodeWrapper.CODE_CONSTRAINT;

@Entity(name = "used_codes")
@NamedQueries({
        @NamedQuery(name = "CodeWrapper.findUsed", query = "SELECT ut FROM used_codes ut WHERE ut.used=true"),
        @NamedQuery(name = "CodeWrapper.findUnused", query = "SELECT ut FROM used_codes ut WHERE ut.used=false"),
        @NamedQuery(name = "CodeWrapper.findByCode", query = "SELECT ut FROM used_codes ut WHERE ut.code=:code"),
})
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code", name = CODE_CONSTRAINT),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeWrapper {
    public static final String CODE_CONSTRAINT = "used_codes_code_unique_constraint";

    @Getter
    @Id
    @SequenceGenerator(name = "USED_CODES_SEQ_GEN", sequenceName = "used_codes_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USED_CODES_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "code", updatable = false, nullable = false, unique = true)
    private String code;

    @Getter
    @Column(name = "creation_date_time", updatable = false, nullable = false)
    @NotNull(message = CONSTRAINT_NOT_NULL)
    private LocalDateTime creationDateTime;

    @Column(name = "used", nullable = false)
    private boolean used;

    @JoinColumn(name = "account_id", updatable = false, nullable = false)
    @OneToOne(cascade = {CascadeType.PERSIST})
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    private Account account;

    @PrePersist
    private void prePersist() {
        creationDateTime = LocalDateTime.now();
    }
}
