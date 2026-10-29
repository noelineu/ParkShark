package com.switchfully.parkshark.parkinglot.api.dto;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContactPersonDto(
        @NotBlank String name,
        String mobilePhone,
        String telephone,
        @NotBlank @Email String email,
        @NotNull @Valid AddressDto address) {
}
