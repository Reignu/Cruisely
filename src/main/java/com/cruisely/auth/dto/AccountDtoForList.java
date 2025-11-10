package com.cruisely.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.security.EntityIdentitySignerVerifier;
import com.cruisely.security.SignableEntity;
import com.cruisely.validators.FirstName;
import com.cruisely.validators.Login;
import com.cruisely.validators.Name;
import com.cruisely.validators.SecondName;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Set;

import static com.cruisely.common.I18n.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccountDtoForList implements SignableEntity {

    @Login
    private String login;

    @FirstName
    private String firstName;

    @SecondName
    private String secondName;

    @Email(message = REGEX_INVALID_EMAIL)
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String email;

    private boolean darkMode;

    private boolean active;

    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long version;

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private Set<AccessLevelType> accessLevels;

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String etag;

    public AccountDtoForList(@Login String login, @FirstName String firstName, @SecondName String secondName, @Email(message = REGEX_INVALID_EMAIL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
            String email, @NotNull(message = CONSTRAINT_NOT_NULL) boolean active, @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO) long version, @NotNull(message = CONSTRAINT_NOT_NULL) Set<AccessLevelType> accessLevels, String etag) {
        this.login = login;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.active = active;
        this.version = version;
        this.accessLevels = accessLevels;
        this.etag = etag;
    }

    @JsonIgnore
    @Override
    public String getSignablePayload() {
        return login + "." + version;
    }

    public AccountDtoForList(@Login String login, @FirstName String firstName, @SecondName String secondName, @Email(message = REGEX_INVALID_EMAIL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
            String email, @NotNull(message = CONSTRAINT_NOT_NULL) boolean active, @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO) long version, @NotNull(message = CONSTRAINT_NOT_NULL) Set<AccessLevelType> accessLevels) throws BaseAppException {
        this.login = login;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.active = active;
        this.version = version;
        this.accessLevels = accessLevels;
        this.etag = EntityIdentitySignerVerifier.calculateEntitySignature(this);
    }
}
