package com.cruisely.auth.dto.detailsview.accesslevels;

import lombok.NoArgsConstructor;
import lombok.ToString;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.auth.dto.detailsview.AccessLevelDetailsViewDto;

@NoArgsConstructor
@ToString
public class ModeratorDetailsViewDto extends AccessLevelDetailsViewDto {
    public ModeratorDetailsViewDto(boolean enabled, long accLevelVersion) {
        super(enabled, AccessLevelType.MODERATOR, accLevelVersion);
    }
}
