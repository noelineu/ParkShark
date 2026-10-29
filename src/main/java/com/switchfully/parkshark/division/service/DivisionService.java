package com.switchfully.parkshark.division.service;

import com.switchfully.parkshark.division.api.DivisionMapper;
import com.switchfully.parkshark.division.api.dto.CreateDivisionDto;
import com.switchfully.parkshark.division.api.dto.DivisionDto;
import com.switchfully.parkshark.division.domain.Director;
import com.switchfully.parkshark.division.domain.DirectorRepository;
import com.switchfully.parkshark.division.domain.Division;
import com.switchfully.parkshark.division.domain.DivisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DivisionService {
    private final DivisionRepository divisionRepository;
    private final DivisionMapper divisionMapper;
    private final DirectorRepository directorRepository;

    public DivisionService(DivisionRepository divisionRepository, DivisionMapper divisionMapper, DirectorRepository directorRepository) {
        this.divisionRepository = divisionRepository;
        this.divisionMapper = divisionMapper;
        this.directorRepository = directorRepository;
    }

    public DivisionDto createDivision(CreateDivisionDto dto) {

        Director director = directorRepository.findByFirstNameAndLastName(dto.directorDto().firstName(), dto.directorDto().lastName());

        if (director == null) {
            director = directorRepository.save(new Director(dto.directorDto().firstName(), dto.directorDto().lastName()));
        }

        Division division = divisionMapper.toDomain(dto, director);
        divisionRepository.save(division);
        return divisionMapper.toDto(division);
    }
}
