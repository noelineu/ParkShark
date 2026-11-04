package com.switchfully.parkshark.parkinglot.api;

import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.domain.Status;
import com.switchfully.parkshark.parkinglot.service.ParkingAllocationService;
import com.switchfully.parkshark.parkinglot.service.ParkingLotService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/allocations", produces = MediaType.APPLICATION_JSON_VALUE)
public class AllocationController {

    private final ParkingAllocationService parkingAllocationService;

    public AllocationController(ParkingAllocationService parkingAllocationService) {
        this.parkingAllocationService = parkingAllocationService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingAllocationDto createAllocation(@Valid @RequestBody CreateParkingAllocationDto createParkingAllocationDto) {
        return parkingAllocationService.createParkingAllocation(createParkingAllocationDto);
    }

    @GetMapping(produces = "application/json")
    public List<ParkingAllocationDto> getAllParkingAllocations(@RequestParam(name = "limit", required = false) Integer limit,
                                                               @RequestParam(name = "status", required = false) Status status,
                                                               @RequestParam(name = "order", defaultValue = "ASC") Sort.Direction order) {
        return parkingAllocationService.getAllParkingAllocations(limit, status, order);
    }

}
