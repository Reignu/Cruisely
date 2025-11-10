package com.cruisely.auth.dto.detailsview.accesslevels;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.auth.dto.detailsview.AccessLevelDetailsViewDto;
import com.cruisely.validators.CompanyName;
import com.cruisely.validators.PhoneNumber;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class BusinessWorkerDetailsViewDto extends AccessLevelDetailsViewDto {

    @PhoneNumber
    private String phoneNumber;

    private boolean confirmed;

    @CompanyName
    private String companyName;


    public BusinessWorkerDetailsViewDto(boolean enabled, String phoneNumber, boolean confirmed, String companyName, long accLevelVersion) {
        super(enabled, AccessLevelType.BUSINESS_WORKER, accLevelVersion);
        this.phoneNumber = phoneNumber;
        this.confirmed = confirmed;
        this.companyName = companyName;
    }
}
