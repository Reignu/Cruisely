package com.cruisely.cruise.dto.cruiseGroups;

import lombok.*;
import com.cruisely.common.I18n;
import com.cruisely.cruise.dto.cruises.CruiseAddressDto;
import com.cruisely.cruise.dto.cruises.CruisePictureDto;
import com.cruisely.validators.CompanyName;
import com.cruisely.validators.CruiseGroupName;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;
import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddCruiseGroupDto {
    @CompanyName
    private String companyName;

    @CruiseGroupName
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String name;

    @Positive(message = CONSTRAINT_POSITIVE)
    private long numberOfSeats;

    @Positive(message = CONSTRAINT_POSITIVE)
    private double price;

    @Valid
    private CruiseAddressDto cruiseAddress;

    private List<CruisePictureDto> cruisePictures;

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String description;


}
