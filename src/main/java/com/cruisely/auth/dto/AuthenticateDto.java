package com.cruisely.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.validators.Login;
import com.cruisely.validators.Password;

import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AuthenticateDto {

    @Login
    private String login;

    @Password
    private String password;

    private Boolean darkMode;

    private String language;

    public Credential toCredential() {
        return new UsernamePasswordCredential(login, password);
    }
}