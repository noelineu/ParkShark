package com.switchfully.parkshark.parkinglot.api;

import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingLotDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingLotDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingLotSummaryDto;
import com.switchfully.parkshark.parkinglot.service.ParkingLotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/parking-lots", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingLotDto createParkingLot(@Valid @RequestBody CreateParkingLotDto createParkingLotDto) {
        return parkingLotService.createParkingLot(createParkingLotDto);
    }

    @GetMapping
    public List<ParkingLotSummaryDto> getAllParkingLots() {
        return parkingLotService.getAllParkingLots();
    }
}
