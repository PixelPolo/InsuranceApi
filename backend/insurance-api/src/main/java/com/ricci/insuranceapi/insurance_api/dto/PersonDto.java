package com.ricci.insuranceapi.insurance_api.dto;

import java.time.LocalDate;
import com.ricci.insuranceapi.insurance_api.validation.ValidationMessage;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersonDto extends ClientDto {

    @PastOrPresent(message = ValidationMessage.PAST_OR_PRESENT)
    private LocalDate birthdate;

}
