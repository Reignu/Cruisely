package com.cruisely.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import com.cruisely.entities.auth.LanguageType;
import com.cruisely.security.SignableEntity;
import com.cruisely.validators.FirstName;
import com.cruisely.validators.Login;
import com.cruisely.validators.Name;
import com.cruisely.validators.SecondName;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static com.cruisely.common.I18n.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AdministratorDto implements SignableEntity {
    @Login
    private String login;

    @FirstName
    private String firstName;

    @SecondName
    private String secondName;

    @Email(message = REGEX_INVALID_EMAIL)
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String email;

    @NotNull(message = CONSTRAINT_NOT_NULL)
    private LanguageType languageType;

    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long version;

    @Override
    @JsonIgnore
    public String getSignablePayload() {
        return login + '.' + version;
    }
}
