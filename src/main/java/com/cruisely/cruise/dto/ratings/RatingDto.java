package com.cruisely.cruise.dto.ratings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.cruisely.validators.FirstName;
import com.cruisely.validators.Login;
import com.cruisely.validators.Name;
import com.cruisely.validators.SecondName;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
    private String cruiseGroupUUID;

    @Max(value = 5, message = RATING_CONSTRAINT_ERROR)
    @Min(value = 1, message = RATING_CONSTRAINT_ERROR)
    private Double rating;

    @Login
    private String login;

    private String name;

    @FirstName
    private String accountFirstName;

    @SecondName
    private String accountSecondName;
}
