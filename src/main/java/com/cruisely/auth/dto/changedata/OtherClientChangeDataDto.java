package com.cruisely.auth.dto.changedata;


import lombok.*;
import com.cruisely.auth.dto.AbstractAccountDto;
import com.cruisely.validators.PhoneNumber;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE_OR_ZERO;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OtherClientChangeDataDto extends AbstractAccountDto {
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    private OtherAddressChangeDto newAddress;
    @PhoneNumber
    private String newPhoneNumber;
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long accVersion;

    public OtherClientChangeDataDto(String login, long version, String newPhoneNumber,
                                    OtherAddressChangeDto newAddress, long accVersion) {
        super(login, version);
        this.newPhoneNumber = newPhoneNumber;
        this.newAddress = newAddress;
        this.accVersion = accVersion;
    }
}