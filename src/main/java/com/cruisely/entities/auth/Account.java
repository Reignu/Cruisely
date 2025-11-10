package com.cruisely.entities.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.common.BaseEntity;
import com.cruisely.entities.auth.wrappers.LanguageTypeWrapper;
import com.cruisely.validators.FirstName;
import com.cruisely.validators.Login;
import com.cruisely.validators.Name;
import com.cruisely.validators.SecondName;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.cruisely.common.I18n.*;
import static com.cruisely.entities.auth.Account.EMAIL_CONSTRAINT;
import static com.cruisely.entities.auth.Account.LOGIN_CONSTRAINT;

@Entity(name = "accounts")
@NamedQueries({
        @NamedQuery(name = "Account.findByLogin", query = "SELECT acc FROM accounts acc WHERE acc.login = :login"),
        @NamedQuery(name = "Account.findById", query = "SELECT acc FROM accounts acc WHERE acc.id = :id"),
        @NamedQuery(name = "Account.findUnconfirmedAccounts", query = "SELECT acc FROM accounts acc WHERE acc.confirmed = false"),
        @NamedQuery(name = "Account.isEmailPresent", query = "SELECT acc FROM accounts acc WHERE acc.email = :email"),
})
@Table(
        indexes = {
                @Index(name = "accounts_language_type_id_index", columnList = "language_type_id"),
                @Index(name = "accounts_created_by_id_index", columnList = "created_by_id"),
                @Index(name = "accounts_altered_by_id_index", columnList = "altered_by_id"),
                @Index(name = "accounts_alter_type_id_index", columnList = "alter_type_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "login", name = LOGIN_CONSTRAINT),
                @UniqueConstraint(columnNames = "email", name = EMAIL_CONSTRAINT)}
)
@ToString
public class Account extends BaseEntity {
    public static final String LOGIN_CONSTRAINT = "accounts_login_unique_constraint";
    public static final String EMAIL_CONSTRAINT = "accounts_email_unique_constraint";

    @Getter
    @Id
    @SequenceGenerator(name = "ACCOUNT_SEQ_GEN", sequenceName = "account_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNT_SEQ_GEN")
    @ToString.Exclude
    private long id;

    @Getter
    @Setter
    @FirstName
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Getter
    @Setter
    @SecondName
    @Column(name = "second_name", nullable = false)
    private String secondName;

    @Getter
    @Setter
    @Login
    @Column(name = "login", nullable = false, unique = true, updatable = false)
    private String login;

    @Getter
    @Setter
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = REGEX_INVALID_EMAIL)
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String email;

    @Getter
    @Setter
    @Column(name = "password_hash", nullable = false)
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @ToString.Exclude
    private String passwordHash;

    @Getter
    @Setter
    @Column(name = "confirmed", nullable = false)
    private boolean confirmed;

    @Getter
    @Setter
    @Column(name = "active", nullable = false)
    private boolean active;

    @Getter
    @Setter
    @Column(name = "dark_mode", nullable = false)
    private boolean darkMode;

    @Getter
    @Setter
    @Column(name = "last_incorrect_authentication_date_time")
    private LocalDateTime lastIncorrectAuthenticationDateTime;

    @Getter
    @Setter
    @Column(name = "last_incorrect_authentication_logical_address")
    private String lastIncorrectAuthenticationLogicalAddress;

    @Getter
    @Setter
    @JoinColumn(name = "language_type_id")
    @OneToOne(cascade = {CascadeType.PERSIST})
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    @ToString.Exclude
    private LanguageTypeWrapper languageType;

    @Getter
    @Setter
    @Column(name = "last_correct_authentication_date_time")
    private LocalDateTime lastCorrectAuthenticationDateTime;

    @Getter
    @Setter
    @Column(name = "last_correct_authentication_logical_address")
    private String lastCorrectAuthenticationLogicalAddress;

    @Getter
    @Setter
    @Column(name = "number_of_authentication_failures")
    private int numberOfAuthenticationFailures;

    @Getter
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE}, mappedBy = "account")
    @Valid
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @ToString.Exclude
    private final Set<AccessLevel> accessLevels = new HashSet<>();

    public void setAccessLevel(AccessLevel accessLevel) {
        accessLevels.add(accessLevel);
    }

    public Account() {
    }

    public Account(String firstName, String secondName, String login, String email,
                   String passwordHash, LanguageTypeWrapper languageType) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
        this.confirmed = false;
        this.active = true;
        this.languageType = languageType;
        this.setCreatedBy(this); // Account is set as self-owner by default
        this.setAlteredBy(this);
    }

    public Account(String firstName, String secondName, String login, String email, String passwordHash,
                   boolean confirmed, boolean active, LanguageTypeWrapper languageType, Account createdBy, Account alteredBy) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
        this.confirmed = confirmed;
        this.active = active;
        this.languageType = languageType;
        this.setCreatedBy(createdBy);
        this.setAlteredBy(alteredBy);
    }


    public Account(String firstName, String secondName, String login, String email, String passwordHash,
                   boolean confirmed, boolean active, LanguageTypeWrapper languageType) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
        this.confirmed = confirmed;
        this.active = active;
        this.languageType = languageType;
        this.setCreatedBy(this);
        this.setAlteredBy(this);
    }

    @Override
    public Long getIdentifier() {
        return id;
    }
}
