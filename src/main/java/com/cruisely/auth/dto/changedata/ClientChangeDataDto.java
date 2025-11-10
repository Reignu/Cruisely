package com.cruisely.auth.dto.changedata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.cruisely.auth.dto.AddressDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ClientChangeDataDto extends ConsumerChangeDataDto {
    @NotNull(message = CONSTRAINT_NOT_NULL)
    @Valid
    private AddressDto newAddress;

    public ClientChangeDataDto(String login, long version, String newFirstName,
                               String newSecondName, String newPhoneNumber, AddressDto newAddress) {
        super(login, version, newFirstName, newSecondName, newPhoneNumber);

        this.newAddress = newAddress;
    }
}
