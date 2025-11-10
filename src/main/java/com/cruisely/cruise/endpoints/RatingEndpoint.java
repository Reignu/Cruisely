package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.BaseEndpoint;
import com.cruisely.common.mappers.MetadataMapper;
import com.cruisely.entities.cruise.Rating;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.CreateRatingDto;
import com.cruisely.cruise.dto.ratings.ClientRatingDto;
import com.cruisely.cruise.dto.ratings.RatingDto;
import com.cruisely.cruise.endpoints.converters.RatingMapper;
import com.cruisely.cruise.managers.RatingManagerLocal;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static com.cruisely.common.I18n.RATING_MAPPER_UUID_PARSE;

@TransactionAttribute(REQUIRES_NEW)
@Interceptors(TrackingInterceptor.class)
@Stateful
public class RatingEndpoint extends BaseEndpoint implements RatingEndpointLocal {

    @Inject
    RatingManagerLocal ratingManager;

    @RolesAllowed("createRating")
    @Override
    public void createRating(CreateRatingDto ratingDto) throws BaseAppException {
        ratingManager.createRating(UUID.fromString(ratingDto.getCruiseGroupUUID()), ratingDto.getRating());
    }

    @RolesAllowed("removeRating")
    @Override
    public void removeRating(UUID cruiseGroupUUID) throws BaseAppException {
        ratingManager.removeRating(cruiseGroupUUID);
    }

    @RolesAllowed("ownFindRating")
    @Override
    public List<RatingDto> getOwnRatings() throws BaseAppException {
        try {
            return ratingManager.getOwnRatings().stream().map(RatingMapper::toRatingDto).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new MapperException(RATING_MAPPER_UUID_PARSE);
        }
    }

    @RolesAllowed("getClientRating")
    public List<ClientRatingDto> getClientRatings(String login) throws BaseAppException {
        List<ClientRatingDto> res = new ArrayList<>();
        for (Rating rating : ratingManager.getClientRatings(login)) {
            res.add(RatingMapper.toClientRatingDto(login, rating));
        }
        return res;
    }

    @RolesAllowed("removeClientRating")
    @Override
    public void removeClientRating(String login, UUID cruiseGroupUUID) throws BaseAppException {
        ratingManager.removeClientRating(login, cruiseGroupUUID);
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public MetadataDto getRatingMetadata(UUID uuid) throws BaseAppException {
        return MetadataMapper.toMetadataDto(ratingManager.findByUuid(uuid));
    }

}
