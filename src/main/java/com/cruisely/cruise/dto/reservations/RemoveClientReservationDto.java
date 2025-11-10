package com.cruisely.cruise.dto.reservations;

import com.fasterxml.jackson.databind.JavaType;
import lombok.*;
import com.cruisely.security.SignableEntity;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RemoveClientReservationDto {
    private String reservationUuid;
    private String clientLogin;
}
