package com.switchfully.parkshark.parkinglot.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateParkingAllocationDto(@NotNull Integer memberId, @NotNull Long parkingLotId, @NotBlank String licensePlateNumber) {}
