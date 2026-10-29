package com.switchfully.parkshark.address.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PostalCodeDto(
        @NotBlank String zip,
        @NotBlank String label) {
}
