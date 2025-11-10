package com.cruisely.auth.dto.changedata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BusinessWorkerChangeDataDto extends ConsumerChangeDataDto {

    public BusinessWorkerChangeDataDto(String login, long version, String newFirstName, String newSecondName, String newPhoneNumber) {
        super(login, version, newFirstName, newSecondName, newPhoneNumber);
    }
}
