package com.cruisely.cruise.dto.attractions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.security.SignableEntity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static com.cruisely.common.I18n.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditAttractionDto implements SignableEntity {

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String uuid;

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String newName;

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String newDescription;

    @Positive(message = CONSTRAINT_POSITIVE)
    private double newPrice;

    @Positive(message = CONSTRAINT_POSITIVE)
    private int newNumberOfSeats;
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long version;

    @JsonIgnore
    @Override
    public String getSignablePayload() {
        return uuid + "." + version;
    }
}
