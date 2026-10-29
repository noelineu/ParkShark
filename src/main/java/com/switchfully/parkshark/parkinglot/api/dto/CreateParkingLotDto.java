package com.switchfully.parkshark.parkinglot.api.dto;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.parkinglot.domain.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateParkingLotDto(
        @NotBlank String name,
        @NotNull Category category,
        @Positive int maxCapacity,
        @NotNull @PositiveOrZero BigDecimal pricePerHour,
        @NotNull @Valid ContactPersonDto contactPerson,
        @NotNull @Valid AddressDto address,
        @NotNull Long divisionId) {
}
