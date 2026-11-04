package com.switchfully.parkshark.parkinglot.api;

import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.api.dto.StopParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.domain.Status;
import com.switchfully.parkshark.parkinglot.service.ParkingAllocationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/allocations", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParkingAllocationController {

    private final ParkingAllocationService parkingAllocationService;

    public ParkingAllocationController(ParkingAllocationService parkingAllocationService) {
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

    @GetMapping(value = "/members/{memberId}", produces = "application/json")
    public List<ParkingAllocationDto> getAllParkingAllocationsByMember(@PathVariable Long memberId,
                                                                       @RequestParam(name = "status", required = false) Status status) {
        return parkingAllocationService.getAllParkingLotsByMember(memberId, status);
    }

    @PostMapping(path = "/{allocationId}/stop", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ParkingAllocationDto stopAllocation(@PathVariable Long allocationId, @Valid @RequestBody StopParkingAllocationDto stopParkingAllocationDto) {
        return parkingAllocationService.stopParkingAllocation(allocationId, stopParkingAllocationDto);
    }

}
