package com.cruisely.auth.dto.changedata;

import lombok.*;
import com.cruisely.auth.dto.AbstractAccountDto;
import com.cruisely.validators.FirstName;
import com.cruisely.validators.Name;
import com.cruisely.validators.SecondName;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountChangeDataDto extends AbstractAccountDto {
    @FirstName
    private String newFirstName;

    @SecondName
    private String newSecondName;

    public AccountChangeDataDto(String login, long version, String newFirstName, String newSecondName) {
        super(login, version);

        this.newFirstName = newFirstName;
        this.newSecondName = newSecondName;
    }
}
