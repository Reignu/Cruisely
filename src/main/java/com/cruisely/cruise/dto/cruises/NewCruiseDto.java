package com.cruisely.cruise.dto.cruises;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ejb.Local;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCruiseDto {

    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String startDate;
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String endDate;
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String cruiseGroupUUID;
}
