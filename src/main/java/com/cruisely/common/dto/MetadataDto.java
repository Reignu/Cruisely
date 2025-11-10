package com.cruisely.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.cruisely.entities.common.AlterType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MetadataDto {

    private LocalDateTime creationDateTime;

    private LocalDateTime lastAlterDateTime;

    private String createdBy;

    private String alteredBy;

    private AlterType alterType;

    private long version;
}
