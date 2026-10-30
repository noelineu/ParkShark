package com.switchfully.parkshark.division.api;

import com.switchfully.parkshark.division.api.dto.CreateDivisionDto;
import com.switchfully.parkshark.division.api.dto.DivisionDto;
import com.switchfully.parkshark.division.service.DivisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "divisions")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public DivisionDto createDivision(@Valid @RequestBody CreateDivisionDto createDivisionDto) {
        return divisionService.createDivision(createDivisionDto);
    }

    @GetMapping(produces = "application/json")
    public List<DivisionDto> getAllDivisions() {
        return divisionService.getAllDivisions();
    }
}
