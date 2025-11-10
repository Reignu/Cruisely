package com.cruisely.auth.dto.changedata;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.auth.dto.AbstractAccountDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;
import static com.cruisely.common.I18n.REGEX_INVALID_EMAIL;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AccountChangeEmailDto extends AbstractAccountDto {
    @Email(message = REGEX_INVALID_EMAIL)
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String newEmail;

    public AccountChangeEmailDto(String login, long version, String newEmail) {
        super(login, version);

        this.newEmail = newEmail;
    }
}
