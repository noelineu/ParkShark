package com.switchfully.parkshark.parkinglot.service;

import com.switchfully.parkshark.division.domain.Division;
import com.switchfully.parkshark.division.domain.DivisionRepository;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.api.dto.*;
import com.switchfully.parkshark.parkinglot.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ParkingLotService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotService.class);

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final DivisionRepository divisionRepository;
    private final MemberRepository memberRepository;
    private final ParkingLotAllocationRepository parkingLotAllocationRepository;
    private final ParkingAllocationMapper parkingAllocationMapper;

    public ParkingLotService(ParkingLotRepository parkingLotRepository, ParkingLotMapper parkingLotMapper, DivisionRepository divisionRepository, MemberRepository memberRepository, ParkingLotAllocationRepository parkingLotAllocationRepository, ParkingAllocationMapper parkingAllocationMapper) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingLotMapper = parkingLotMapper;
        this.divisionRepository = divisionRepository;
        this.memberRepository = memberRepository;
        this.parkingLotAllocationRepository = parkingLotAllocationRepository;
        this.parkingAllocationMapper = parkingAllocationMapper;
    }

    public ParkingLotDto createParkingLot(CreateParkingLotDto dto) {
        Division division = divisionRepository.findById(dto.divisionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Division with id " + dto.divisionId() + " does not exist"));

        ParkingLot parkingLot = parkingLotRepository.save(parkingLotMapper.toDomain(dto, division));
        logger.info("Created parking lot {} with id {}", parkingLot.getName(), parkingLot.getId());
        return parkingLotMapper.toDto(parkingLot);
    }

    public List<ParkingLotSummaryDto> getAllParkingLots() {
        List<ParkingLot> summary = parkingLotRepository.findAll();
        return summary.stream().map(parkingLotMapper::toSummaryDto).toList();
    }

    private boolean hasCapacity(ParkingLot parkingLot) {
        int occupiedSpaces = parkingLotAllocationRepository.countParkingAllocationByParkingLotAndStatus(parkingLot, Status.ACTIVE);
        return parkingLot.getMaxCapacity() > occupiedSpaces;
    }

    public ParkingAllocationDto createParkingAllocation(CreateParkingAllocationDto dto) {
        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member with id " + dto.memberId() + " does not exist"));

        ParkingLot parkingLot = parkingLotRepository.findById(dto.parkingLotId())
                .orElseThrow(() -> new IllegalArgumentException("Parking lot with id " + dto.parkingLotId() + " does not exist"));

        if (!hasCapacity(parkingLot)) {
            throw new IllegalArgumentException("Parking lot " + parkingLot.getId() + " is full");
        }

        ParkingAllocation allocation = parkingLotAllocationRepository.save(parkingAllocationMapper.toDomain(dto, member, parkingLot));
        return parkingAllocationMapper.toDto(allocation);

        }
    }
