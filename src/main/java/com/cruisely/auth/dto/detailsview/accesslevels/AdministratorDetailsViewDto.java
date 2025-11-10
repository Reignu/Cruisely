package com.cruisely.auth.dto.detailsview.accesslevels;

import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.auth.dto.detailsview.AccessLevelDetailsViewDto;


public class AdministratorDetailsViewDto extends AccessLevelDetailsViewDto {
    public AdministratorDetailsViewDto(boolean enabled, long accLevelVersion) {
        super(enabled, AccessLevelType.ADMINISTRATOR, accLevelVersion);
    }

    public AdministratorDetailsViewDto() {
    }
}
