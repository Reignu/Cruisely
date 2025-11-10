package com.cruisely.cruise.dto.reservations;

import lombok.*;
import com.cruisely.validators.PhoneNumber;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SelfReservationDto {
    private UUID uuid;

    private String CruiseName;

    private List<String> attractions;

    private String startDate;

    private String endDate;

    @PhoneNumber
    private String phoneNumber;

    @Positive
    private long numberOfSeats;

    @Positive
    private Double price;
}
