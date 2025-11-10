package com.cruisely.auth.dto.changedata;


import lombok.*;
import com.cruisely.auth.dto.AbstractAccountDto;
import com.cruisely.validators.PhoneNumber;

import javax.validation.constraints.PositiveOrZero;

import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE_OR_ZERO;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OtherBusinessWorkerChangeDataDto extends AbstractAccountDto {
    @PhoneNumber
    private String newPhoneNumber;
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long accVersion;

    public OtherBusinessWorkerChangeDataDto(String login, long version, String newPhoneNumber, long accVersion) {
        super(login, version);
        this.newPhoneNumber = newPhoneNumber;
        this.accVersion = accVersion;
    }
}
