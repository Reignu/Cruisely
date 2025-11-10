package com.cruisely.auth.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.cruisely.entities.auth.LanguageType;
import com.cruisely.validators.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorForRegistrationDto {

    @FirstName
    private String firstName;

    @SecondName
    private String secondName;

    @Login
    private String login;

    @Email(message = REGEX_INVALID_EMAIL)
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String email;

    @Password
    @ToString.Exclude
    private String password;

    @NotNull(message = CONSTRAINT_NOT_NULL)
    private LanguageType languageType;
}
