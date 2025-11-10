package com.cruisely.cruise.dto.cruiseGroups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.security.SignableEntity;

import javax.validation.constraints.PositiveOrZero;

import java.util.UUID;

import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE_OR_ZERO;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeactivateCruiseGroupDto implements SignableEntity{

    private UUID uuid;

    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long version;

    @JsonIgnore
    @Override
    public String getSignablePayload() {
        return uuid + "." + version; }

}





