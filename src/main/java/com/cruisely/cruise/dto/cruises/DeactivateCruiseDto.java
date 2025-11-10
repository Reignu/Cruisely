package com.cruisely.cruise.dto.cruises;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.security.SignableEntity;

import javax.validation.constraints.PositiveOrZero;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeactivateCruiseDto implements SignableEntity {

    private String uuid;
    @PositiveOrZero
    private Long version;


    @JsonIgnore
    @Override
    public String getSignablePayload() {
        return uuid + "." + version;
    }
}
