package com.cruisely.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.cruisely.entities.common.AlterType;
import com.cruisely.entities.auth.wrappers.LanguageTypeWrapper;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountMetadataDto {
    private LocalDateTime creationDateTime;

    private LocalDateTime lastAlterDateTime;

    private String createdBy;

    private String alteredBy;

    private AlterType alterType;

    private long version;

    private LocalDateTime lastIncorrectAuthenticationDateTime;

    private String lastIncorrectAuthenticationLogicalAddress;

    private LocalDateTime lastCorrectAuthenticationDateTime;

    private String lastCorrectAuthenticationLogicalAddress;

    private int numberOfAuthenticationFailures;

    private String languageType;
}
