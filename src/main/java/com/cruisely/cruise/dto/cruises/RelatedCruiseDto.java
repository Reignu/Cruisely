package com.cruisely.cruise.dto.cruises;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedCruiseDto {
    private UUID uuid;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
