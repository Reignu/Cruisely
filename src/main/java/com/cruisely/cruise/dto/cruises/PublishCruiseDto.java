package com.cruisely.cruise.dto.cruises;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cruisely.security.SignableEntity;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishCruiseDto implements SignableEntity {
    private long cruiseVersion;
    private String cruiseUuid;

    @Override
    public String getSignablePayload() {
        return cruiseUuid+"."+cruiseVersion;
    }
}
