package com.switchfully.parkshark.parkinglot.service;

import com.switchfully.parkshark.division.domain.DivisionRepository;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.domain.*;

import java.util.List;

public class ParkingAllocationService {

    private final ParkingLotRepository parkingLotRepository;
    private final MemberRepository memberRepository;
    private final ParkingLotAllocationRepository parkingLotAllocationRepository;
    private final ParkingAllocationMapper parkingAllocationMapper;

    public ParkingAllocationService(ParkingLotRepository parkingLotRepository, MemberRepository memberRepository,
                                    ParkingLotAllocationRepository parkingLotAllocationRepository, ParkingAllocationMapper parkingAllocationMapper) {
        this.parkingLotRepository = parkingLotRepository;
        this.memberRepository = memberRepository;
        this.parkingLotAllocationRepository = parkingLotAllocationRepository;
        this.parkingAllocationMapper = parkingAllocationMapper;
    }

    public ParkingAllocationDto createParkingAllocation(CreateParkingAllocationDto dto) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member with id " + dto.memberId() + " does not exist"));

        ParkingLot parkingLot = parkingLotRepository.findById(dto.parkingLotId())
                .orElseThrow(() -> new IllegalArgumentException("Parking lot with id " + dto.parkingLotId() + " does not exist"));

        if (!(hasCapacity(parkingLot))) {
            throw new RuntimeException();
        }

        ParkingAllocation allocation = parkingLotAllocationRepository.save(parkingAllocationMapper.toDomain(dto, member, parkingLot));
        return parkingAllocationMapper.toDto(allocation);

    }

    public List<ParkingAllocationDto> getAllParkingAllocations(int limit, String s)

    private boolean hasCapacity(ParkingLot parkingLot) {
        int occupiedSpaces = parkingLotAllocationRepository.countParkingAllocationByParkingLotAndStatus(parkingLot, Status.ACTIVE);
        return parkingLot.getMaxCapacity() > occupiedSpaces;
    }
}
