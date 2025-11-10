package com.cruisely.auth.dto.detailsview;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.entities.auth.LanguageType;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.security.EntityIdentitySignerVerifier;
import com.cruisely.security.SignableEntity;
import com.cruisely.validators.FirstName;
import com.cruisely.validators.Login;
import com.cruisely.validators.Name;
import com.cruisely.validators.SecondName;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Set;

import static com.cruisely.common.I18n.*;

@Data
@NoArgsConstructor
public class AccountDetailsViewDto implements SignableEntity {
    @FirstName
    private String firstName;

    @SecondName
    private String secondName;

    private boolean darkMode;

    @Login
    private String login;

    @Email(message = REGEX_INVALID_EMAIL)
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String email;

    private boolean confirmed;

    private boolean active;

    @NotNull(message = CONSTRAINT_NOT_NULL)
    private LanguageType languageType;

    @Valid
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private Set<AccessLevelDetailsViewDto> accessLevels;

    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long version;

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String etag;

    public AccountDetailsViewDto(String firstName, String secondName, boolean darkMode, String login, String email, boolean confirmed, boolean active, LanguageType languageType, Set<AccessLevelDetailsViewDto> accessLevels, long version) throws BaseAppException {
        this.firstName = firstName;
        this.secondName = secondName;
        this.darkMode = darkMode;
        this.login = login;
        this.email = email;
        this.confirmed = confirmed;
        this.active = active;
        this.languageType = languageType;
        this.accessLevels = accessLevels;
        this.version = version;
        this.etag = EntityIdentitySignerVerifier.calculateEntitySignature(this);
    }

    @JsonIgnore
    @Override
    public String getSignablePayload() {
        return login + "." + version;
    }
}

