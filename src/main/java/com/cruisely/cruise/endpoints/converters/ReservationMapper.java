package com.cruisely.cruise.endpoints.converters;

import com.cruisely.entities.cruise.Attraction;
import com.cruisely.entities.cruise.Reservation;
import com.cruisely.cruise.dto.attractions.AttractionDto;
import com.cruisely.cruise.dto.reservations.CruiseReservationDto;
import com.cruisely.cruise.dto.reservations.SelfReservationDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for mapping DTO objects to model objects
 */
public class ReservationMapper {
    private ReservationMapper() {
    }

    /**
     * Maps a Reservation object to a CruiseReservationDto
     *
     * @param reservation reservation provided for conversion
     * @return CruiseReservationDto object
     */
    public static CruiseReservationDto toReservationDto(Reservation reservation) {
        List<String> attractions = new ArrayList<>();
        for( Attraction att :reservation.getAttractions()){
            attractions.add(att.getName());
        }
        return new CruiseReservationDto(reservation.getUuid(),reservation.getClient().getAccount().getLogin(), reservation.getNumberOfSeats(), reservation.getPrice(),
                reservation.getCruise().getCruisesGroup().getName(),attractions);
    }

    /**
     * Method mapping a Reservation object to a SelfReservationDto object
     * @param reservation reservation provided for conversion
     * @return SelfReservationDto object
     */
    public static SelfReservationDto toSelfReservationDto(Reservation reservation) {
        List<String> attractions = new ArrayList<>();
        for (Attraction attraction : reservation.getAttractions()) {
            attractions.add(attraction.getName());
        }
        return new SelfReservationDto(reservation.getUuid(), reservation.getCruise().getCruisesGroup().getName(),
                    attractions, reservation.getCruise().getStartDate().toString(), reservation.getCruise().getEndDate().toString(),
                    reservation.getCruise().getCruisesGroup().getCompany().getPhoneNumber(), reservation.getNumberOfSeats(),
                    reservation.getPrice()
                );
    }
}
