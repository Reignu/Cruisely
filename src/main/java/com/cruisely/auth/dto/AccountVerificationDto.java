package com.cruisely.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountVerificationDto {

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String token;
}
