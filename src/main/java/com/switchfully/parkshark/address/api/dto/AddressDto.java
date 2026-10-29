package com.switchfully.parkshark.address.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressDto(
        @NotBlank String streetName,
        @NotBlank String streetNumber,
        @NotNull @Valid PostalCodeDto postalCode) {
}
