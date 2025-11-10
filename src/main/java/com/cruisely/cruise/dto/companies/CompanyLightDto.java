package com.cruisely.cruise.dto.companies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.validators.CompanyName;

import javax.validation.constraints.Positive;

import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompanyLightDto {
    @CompanyName
    private String name;

    @Positive(message = CONSTRAINT_POSITIVE)
    private long nip;
}