package com.cruisely.cruise.dto.reservations;

import lombok.*;
import com.cruisely.cruise.dto.attractions.AttractionDto;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class CruiseReservationDto {

    private UUID uuid;
    private String clientName;
    @Positive
    private long numberOfSeats;
    @Positive
    private Double price;

    private String CruiseName;
    private List<String> attractions;

}
