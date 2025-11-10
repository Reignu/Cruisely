package com.cruisely.auth.dto.detailsview;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.auth.dto.detailsview.accesslevels.AdministratorDetailsViewDto;
import com.cruisely.auth.dto.detailsview.accesslevels.BusinessWorkerDetailsViewDto;
import com.cruisely.auth.dto.detailsview.accesslevels.ClientDetailsViewDto;
import com.cruisely.auth.dto.detailsview.accesslevels.ModeratorDetailsViewDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @Type(value = ClientDetailsViewDto.class, name = "client"),
        @Type(value = BusinessWorkerDetailsViewDto.class, name = "businessWorker"),
        @Type(value = ModeratorDetailsViewDto.class, name = "moderator"),
        @Type(value = AdministratorDetailsViewDto.class, name = "administrator")
})
public abstract class AccessLevelDetailsViewDto {
    private boolean enabled;

    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    private AccessLevelType accessLevelType;

    @PositiveOrZero(message = CONSTRAINT_POSITIVE)
    private long accVersion;

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
