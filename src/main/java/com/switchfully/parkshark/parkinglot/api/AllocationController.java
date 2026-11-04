package com.switchfully.parkshark.parkinglot.api;

import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.service.ParkingLotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/allocations", produces = MediaType.APPLICATION_JSON_VALUE)
public class AllocationController {

    private final ParkingLotService parkingLotService;

    public AllocationController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingAllocationDto createAllocation(@Valid @RequestBody CreateParkingAllocationDto createParkingAllocationDto) {
        return parkingLotService.createParkingAllocation(createParkingAllocationDto);
    }
}
