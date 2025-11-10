package com.cruisely.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.cruisely.validators.Login;
import com.cruisely.validators.Password;

import javax.validation.constraints.NotEmpty;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDto {
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String token;
    @Login
    private String login;

    @ToString.Exclude
    @Password
    private String password;
}
