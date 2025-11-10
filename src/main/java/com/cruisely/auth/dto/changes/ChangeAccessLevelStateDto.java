package com.cruisely.auth.dto.changes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.security.SignableEntity;
import com.cruisely.validators.Login;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE_OR_ZERO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAccessLevelStateDto implements SignableEntity {
    @Login
    private String accountLogin;

    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    private AccessLevelType accessLevel;

    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long accountVersion;

    private boolean enabled;

    @JsonIgnore
    @Override
    public String getSignablePayload() {
        return accountLogin + "." + accountVersion;
    }
}
