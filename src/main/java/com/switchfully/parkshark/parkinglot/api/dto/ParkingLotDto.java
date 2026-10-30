package com.switchfully.parkshark.parkinglot.api.dto;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.division.api.dto.DivisionDto;
import com.switchfully.parkshark.parkinglot.domain.Category;

import java.math.BigDecimal;

public record ParkingLotDto(
        Long id,
        String name,
        Category category,
        int maxCapacity,
        BigDecimal pricePerHour,
        ContactPersonDto contactPerson,
        AddressDto address,
        DivisionDto division) {
}
