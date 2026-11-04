package com.switchfully.parkshark.parkinglot.service;

import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocation;
import com.switchfully.parkshark.parkinglot.domain.ParkingLot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParkingAllocationMapper {
    public ParkingAllocationDto toDto(ParkingAllocation parkingAllocation) {
        return new ParkingAllocationDto(
                parkingAllocation.getId(),
                parkingAllocation.getMember().getId(),
                parkingAllocation.getParkingLot().getId(),
                parkingAllocation.getLicensePlateNumber(),
                parkingAllocation.getStartTime(),
                parkingAllocation.getStopTime(),
                parkingAllocation.getStatus()
        );
    }

    public List<ParkingAllocationDto> toDto(List<ParkingAllocation> parkingAllocationList) {
        return parkingAllocationList.stream()
                .map(this::toDto)
                .toList();
    }

    public ParkingAllocation toDomain(CreateParkingAllocationDto dto, Member member, ParkingLot parkingLot) {
        return new ParkingAllocation(
                member,
                parkingLot,
                dto.licensePlateNumber()
        );
    }
}
