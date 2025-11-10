package com.cruisely.auth.dto.changedata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.validators.PhoneNumber;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ConsumerChangeDataDto extends AccountChangeDataDto {
    @PhoneNumber
    private String newPhoneNumber;

    public ConsumerChangeDataDto(String login, long version, String newFirstName, String newSecondName, String newPhoneNumber) {
        super(login, version, newFirstName, newSecondName);

        this.newPhoneNumber = newPhoneNumber;
    }
}
