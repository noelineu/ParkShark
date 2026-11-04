package com.switchfully.parkshark.parkinglot.service;

import com.switchfully.parkshark.exception.AllocationNotFoundException;
import com.switchfully.parkshark.exception.MemberNotFoundException;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingLotSummaryDto;
import com.switchfully.parkshark.parkinglot.api.dto.StopParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.domain.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ParkingAllocationService {

    private final ParkingLotRepository parkingLotRepository;
    private final MemberRepository memberRepository;
    private final ParkingAllocationRepository parkingAllocationRepository;
    private final ParkingAllocationMapper parkingAllocationMapper;

    public ParkingAllocationService(ParkingLotRepository parkingLotRepository, MemberRepository memberRepository,
                                    ParkingAllocationRepository parkingAllocationRepository, ParkingAllocationMapper parkingAllocationMapper) {
        this.parkingLotRepository = parkingLotRepository;
        this.memberRepository = memberRepository;
        this.parkingAllocationRepository = parkingAllocationRepository;
        this.parkingAllocationMapper = parkingAllocationMapper;
    }

    public ParkingAllocationDto createParkingAllocation(CreateParkingAllocationDto dto) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member with id " + dto.memberId() + " does not exist"));

        ParkingLot parkingLot = parkingLotRepository.findById(dto.parkingLotId())
                .orElseThrow(() -> new IllegalArgumentException("Parking lot with id " + dto.parkingLotId() + " does not exist"));

        if (!hasCapacity(parkingLot)) {
            throw new IllegalArgumentException("Parking lot " + parkingLot.getId() + " is full");
        }

        ParkingAllocation allocation = parkingAllocationRepository.save(parkingAllocationMapper.toDomain(dto, member, parkingLot));
        return parkingAllocationMapper.toDto(allocation);

    }

    public List<ParkingAllocationDto> getAllParkingAllocations(Integer limit, Status status, Sort.Direction order) {

        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("Limit cannot be negative or equal to 0");
        }
        List<ParkingAllocation> parkingAllocations = status == null ? parkingAllocationRepository.findAll() : parkingAllocationRepository.findParkingAllocationsByStatus(status);

        Comparator<ParkingAllocation> byStartTime = Comparator.comparing(ParkingAllocation::getStartTime);

        if (order == Sort.Direction.DESC) {
            byStartTime = byStartTime.reversed();
        }

        return parkingAllocations.stream()
                .sorted(byStartTime)
                .limit(limit == null ? Long.MAX_VALUE : limit)
                .map(parkingAllocationMapper::toDto)
                .toList();
    }

    public List<ParkingAllocationDto> getAllParkingLotsByMember(Long memberId, Status status) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        List<ParkingAllocation> parkingAllocations = status ==
                null ? parkingAllocationRepository.findParkingAllocationsByMember(member) : parkingAllocationRepository.findParkingAllocationsByMemberAndStatus(member, status);

        return parkingAllocations.stream()
                .map(parkingAllocationMapper::toDto)
                .toList();
    }

    public ParkingAllocationDto stopParkingAllocation(Long allocationId, StopParkingAllocationDto dto) {
        ParkingAllocation parkingAllocation = parkingAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new AllocationNotFoundException(allocationId));

        parkingAllocation.stop(dto.memberId());

        return parkingAllocationMapper.toDto(parkingAllocation);
    }

    private boolean hasCapacity(ParkingLot parkingLot) {
        int occupiedSpaces = parkingAllocationRepository.countParkingAllocationByParkingLotAndStatus(parkingLot, Status.ACTIVE);
        return parkingLot.getMaxCapacity() > occupiedSpaces;
    }

}
