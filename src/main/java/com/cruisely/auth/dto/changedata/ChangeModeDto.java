package com.cruisely.auth.dto.changedata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.auth.dto.AbstractAccountDto;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChangeModeDto extends AbstractAccountDto {
    private boolean newMode;

    public ChangeModeDto(String login, Long version, boolean newMode) {
        super(login, version);

        this.newMode = newMode;
    }

}
