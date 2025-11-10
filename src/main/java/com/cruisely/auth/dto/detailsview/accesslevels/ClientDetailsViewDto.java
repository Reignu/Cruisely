package com.cruisely.auth.dto.detailsview.accesslevels;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.auth.dto.AddressDto;
import com.cruisely.auth.dto.detailsview.AccessLevelDetailsViewDto;
import com.cruisely.validators.PhoneNumber;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ClientDetailsViewDto extends AccessLevelDetailsViewDto {
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    private AddressDto address;

    @PhoneNumber
    private String phoneNumber;


    public ClientDetailsViewDto(boolean enabled, AddressDto address, String phoneNumber, long accLevelVersion) {
        super(enabled, AccessLevelType.CLIENT, accLevelVersion);
        this.address = address;
        this.phoneNumber = phoneNumber;

    }
}
