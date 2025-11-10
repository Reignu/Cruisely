package com.cruisely.cruise.dto.companies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.auth.dto.AddressDto;
import com.cruisely.validators.CompanyName;
import com.cruisely.validators.PhoneNumber;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddCompanyDto {
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @CompanyName
    private String name;

    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    private AddressDto addressDto;

    @NotNull(message = CONSTRAINT_NOT_NULL)
    @PhoneNumber
    private String phoneNumber;

    @Positive(message = CONSTRAINT_POSITIVE)
    private long nip;
}
