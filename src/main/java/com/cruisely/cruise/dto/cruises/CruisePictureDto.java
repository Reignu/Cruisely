package com.cruisely.cruise.dto.cruises;

import lombok.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static com.cruisely.common.I18n.CONSTRAINT_POSITIVE_OR_ZERO;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class CruisePictureDto {

    private String dataURL;

    private String pictureName;
    @PositiveOrZero(message = CONSTRAINT_POSITIVE_OR_ZERO)
    private long version;

}
