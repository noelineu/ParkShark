package com.switchfully.parkshark.parkinglot.api.dto;

import jakarta.validation.constraints.NotNull;

public record StopParkingAllocationDto(@NotNull Long memberId) {}
