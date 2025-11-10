package com.cruisely.cruise.dto.cruiseGroups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import com.cruisely.entities.cruise.CruisePicture;
import com.cruisely.cruise.dto.cruises.CruiseAddressDto;
import com.cruisely.cruise.dto.cruises.CruisePictureDto;
import com.cruisely.security.SignableEntity;
import com.cruisely.validators.CruiseGroupName;
import com.cruisely.validators.Name;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.UUID;

import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE;
import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE_OR_ZERO;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class ChangeCruiseGroupDto implements SignableEntity {

    private UUID uuid;

    @CruiseGroupName
    private String name;

    @Positive
    private long numberOfSeats;

    @Positive
    private Double price;

    @Valid
    private CruiseAddressDto cruiseAddress;

    @Valid
    private CruisePictureDto picture;

    @Name
    private String description;
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long version;

    @JsonIgnore
    @Override
    public String getSignablePayload() { return uuid + "." + version; }
}
