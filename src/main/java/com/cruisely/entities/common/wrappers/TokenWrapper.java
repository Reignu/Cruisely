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
import static com.cruisely.entities.common.wrappers.TokenWrapper.TOKEN_CONSTRAINT;

@Entity(name = "used_tokens")
@NamedQueries({
        @NamedQuery(name = "TokenWrapper.findUsed", query = "SELECT ut FROM used_tokens ut WHERE ut.used=true"),
        @NamedQuery(name = "TokenWrapper.findUnused", query = "SELECT ut FROM used_tokens ut WHERE ut.used=false"),
        @NamedQuery(name = "TokenWrapper.findByToken", query = "SELECT ut FROM used_tokens ut WHERE ut.token=:token"),
        @NamedQuery(name = "TokenWrapper.findByAccount", query = "SELECT ut FROM used_tokens  ut WHERE ut.account=:account")
})
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "token", name = TOKEN_CONSTRAINT),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenWrapper {
    public static final String TOKEN_CONSTRAINT = "used_tokens_token_unique_constraint";

    @Getter
    @Id
    @SequenceGenerator(name = "USED_TOKENS_SEQ_GEN", sequenceName = "used_tokens_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USED_TOKENS_SEQ_GEN")
    @ToString.Exclude
private long id;

    @Getter
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @Column(name = "token", updatable = false, nullable = false, unique = true)
    private String token;

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
