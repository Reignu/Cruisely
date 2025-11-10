package com.cruisely.auth.dto.changedata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.cruisely.auth.dto.AbstractAccountDto;
import com.cruisely.validators.Password;

@Getter
@Setter
@NoArgsConstructor
public class AccountChangeOwnPasswordDto extends AbstractAccountDto {
    @Password
    private String oldPassword;

    @Password
    private String newPassword;

    public AccountChangeOwnPasswordDto(String login, long version, String oldPassword, String newPassword) {
        super(login, version);
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
