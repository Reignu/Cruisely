package com.cruisely.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.security.SignableEntity;
import com.cruisely.validators.Login;

import javax.validation.constraints.PositiveOrZero;

import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE_OR_ZERO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockAccountDto implements SignableEntity {
    @Login
    private String login;

    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long version;

    @JsonIgnore
    @Override
    public String getSignablePayload() {
        return login + "." + version;
    }
}
