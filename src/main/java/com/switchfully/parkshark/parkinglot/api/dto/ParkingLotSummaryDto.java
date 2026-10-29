package com.switchfully.parkshark.parkinglot.api.dto;

import com.switchfully.parkshark.parkinglot.domain.ContactPerson;

public record ParkingLotSummaryDto(
        Long id, String name, int maxCapacity, String contactPersonEmail, String contactPersonTelephone) {}