package com.cruisely.cruise.dto.cruiseGroups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.security.EntityIdentitySignerVerifier;
import com.cruisely.security.SignableEntity;
import com.cruisely.cruise.dto.cruises.CruiseAddressDto;
import com.cruisely.cruise.dto.cruises.CruiseForCruiseGroupDto;
import com.cruisely.cruise.dto.cruises.CruisePictureDto;
import com.cruisely.cruise.dto.companies.CompanyLightDto;
import com.cruisely.validators.CompanyName;
import com.cruisely.validators.CruiseGroupName;
import com.cruisely.validators.Name;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_EMPTY;

@NoArgsConstructor
//@AllArgsConstructor
@Data
@Getter
@Setter
public class CruiseGroupWithDetailsDto implements SignableEntity {

        private UUID uuid;

        @CompanyName
        private CompanyLightDto company;

        @CruiseGroupName
        private String name;

        @Positive
        private long numberOfSeats;
        @Positive
        private Double price;

        private CruiseAddressDto cruiseAddress;

        private List<CruisePictureDto> cruisePictures;
        @Positive
        private long version;

        private String description;

        private boolean active;

        private List<CruiseForCruiseGroupDto> cruises;
        private String start_time;
        private String end_time;

        @NotEmpty(message = CONSTRAINT_NOT_EMPTY)
        private String etag;

        @JsonIgnore
        @Override
        public String getSignablePayload() { return uuid + "." + version; }

        public CruiseGroupWithDetailsDto(UUID uuid, CompanyLightDto company, String name, long numberOfSeats,
                                         Double price, CruiseAddressDto cruiseAddress, List<CruisePictureDto> cruisePictures,
                                         long version, String description, boolean active,
                                         List<CruiseForCruiseGroupDto> cruises, String start_time,
                                         String end_time, String etag) {
                this.uuid = uuid;
                this.company = company;
                this.name = name;
                this.numberOfSeats = numberOfSeats;
                this.price = price;
                this.cruiseAddress = cruiseAddress;
                this.cruisePictures = cruisePictures;
                this.version = version;
                this.description = description;
                this.active = active;
                this.cruises = cruises;
                this.start_time = start_time;
                this.end_time = end_time;
                this.etag = etag;
        }

        public CruiseGroupWithDetailsDto(UUID uuid, CompanyLightDto company, String name, long numberOfSeats,
                                         Double price, CruiseAddressDto cruiseAddress, List<CruisePictureDto> cruisePictures,
                                         long version, String description, boolean active,
                                         List<CruiseForCruiseGroupDto> cruises, String start_time,
                                         String end_time) throws BaseAppException {
                this.uuid = uuid;
                this.company = company;
                this.name = name;
                this.numberOfSeats = numberOfSeats;
                this.price = price;
                this.cruiseAddress = cruiseAddress;
                this.cruisePictures = cruisePictures;
                this.version = version;
                this.description = description;
                this.active = active;
                this.cruises = cruises;
                this.start_time = start_time;
                this.end_time = end_time;
                this.etag = EntityIdentitySignerVerifier.calculateEntitySignature(this);
        }
}


