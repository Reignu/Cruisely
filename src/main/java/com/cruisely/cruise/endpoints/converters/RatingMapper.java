package com.cruisely.cruise.endpoints.converters;

import lombok.NoArgsConstructor;
import com.cruisely.entities.cruise.Rating;
import com.cruisely.cruise.dto.ratings.ClientRatingDto;
import com.cruisely.cruise.dto.ratings.RatingDto;

@NoArgsConstructor
public class RatingMapper {
    public static RatingDto toRatingDto(Rating rating) {
        return new RatingDto(rating.getCruiseGroup().getUuid().toString(),
                rating.getRating(), rating.getAccount().getLogin(), rating.getCruiseGroup().getName(), rating.getAccount().getFirstName(), rating.getAccount().getSecondName());
    }

    /**
    * Maps a Rating object and a client's login to a ClientRatingDto object
    *
    * @param login  client's login
    * @param rating Rating object
    * @return ClientRatingDto object
     */
    public static ClientRatingDto toClientRatingDto(String login, Rating rating) {
        return new ClientRatingDto(
                login, rating.getCruiseGroup().getName(), rating.getCruiseGroup().getUuid().toString(), rating.getRating(), rating.getUuid().toString()
        );
    }
}
