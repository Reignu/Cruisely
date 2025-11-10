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
public class ChangeLanguageDto extends AbstractAccountDto {
    public ChangeLanguageDto(String login, Long version) {
        super(login, version);
    }
}
