package com.switchfully.parkshark.parkinglot.service;

import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingLotDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingLotDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingLotSummaryDto;
import com.switchfully.parkshark.parkinglot.domain.ParkingLot;
import com.switchfully.parkshark.parkinglot.domain.ParkingLotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingLotService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotService.class);

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLotMapper parkingLotMapper;

    public ParkingLotService(ParkingLotRepository parkingLotRepository, ParkingLotMapper parkingLotMapper) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingLotMapper = parkingLotMapper;
    }

    @Transactional
    public ParkingLotDto createParkingLot(CreateParkingLotDto dto) {
        ParkingLot parkingLot = parkingLotRepository.save(parkingLotMapper.toDomain(dto));
        logger.info("Created parking lot {} with id {}", parkingLot.getName(), parkingLot.getId());
        return parkingLotMapper.toDto(parkingLot);
    }

    @Transactional(readOnly = true)
    public List<ParkingLotSummaryDto> getAllParkingLots() {
        List<ParkingLot> summary = parkingLotRepository.findAll();
        return summary.stream().map(parkingLotMapper::toSummaryDto).toList();

    }
}
