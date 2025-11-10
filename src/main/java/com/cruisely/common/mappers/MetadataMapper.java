package com.cruisely.common.mappers;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.entities.common.BaseEntity;

public class MetadataMapper {
    private MetadataMapper() {
    }

    public static MetadataDto toMetadataDto(BaseEntity entity) {
        return new MetadataDto(entity.getCreationDateTime(), entity.getLastAlterDateTime(),
                entity.getCreatedBy().getLogin(), entity.getAlteredBy().getLogin(),
                entity.getAlterType().getName(), entity.getVersion());
    }
}
