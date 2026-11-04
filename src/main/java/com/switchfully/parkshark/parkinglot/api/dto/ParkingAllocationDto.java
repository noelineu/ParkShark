package com.switchfully.parkshark.parkinglot.api.dto;

import com.switchfully.parkshark.parkinglot.domain.Status;

import java.time.LocalDateTime;

public record ParkingAllocationDto(Long id, Long memberId, Long parkingLotId, String LicensePlateNumber, LocalDateTime startTime, LocalDateTime stopTime, Status status) {}
