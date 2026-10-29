package com.switchfully.parkshark.division.service;

import com.switchfully.parkshark.division.api.DivisionMapper;
import com.switchfully.parkshark.division.api.dto.CreateDivisionDto;
import com.switchfully.parkshark.division.api.dto.DirectorDto;
import com.switchfully.parkshark.division.api.dto.DivisionDto;
import com.switchfully.parkshark.division.domain.Director;
import com.switchfully.parkshark.division.domain.DirectorRepository;
import com.switchfully.parkshark.division.domain.Division;
import com.switchfully.parkshark.division.domain.DivisionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DivisionServiceTest {

    @Mock
    private DivisionRepository divisionRepository;

    @Mock
    private DivisionMapper divisionMapper;

    @Mock
    private DirectorRepository directorRepository;

    @InjectMocks
    private DivisionService divisionService;

    @Test
    void givenAnExistingDirector_whenCreatingADivision_thenUseExistingDirector() {
        Director director = new Director("Noéline", "LaBourreau");

        CreateDivisionDto createDivisionDto = new CreateDivisionDto(
                "Big entreprise",
                "Small entreprise",
                new DirectorDto("Noéline", "LaBourreau")
        );

        Division division = new Division();
        DivisionDto expectedDto = mock(DivisionDto.class);

        when(directorRepository.findByFirstNameAndLastName(
                "Noéline", "LaBourreau"))
                .thenReturn(director);

        when(divisionMapper.toDomain(createDivisionDto, director))
                .thenReturn(division);

        when(divisionMapper.toDto(division))
                .thenReturn(expectedDto);

        DivisionDto result = divisionService.createDivision(createDivisionDto);

        assertSame(expectedDto, result);

        verify(directorRepository, never()).save(any(Director.class));
        verify(divisionRepository).save(division);
        verify(divisionMapper).toDomain(createDivisionDto, director);
        verify(divisionMapper).toDto(division);
    }

    @Test
    void givenANonExistingDirector_whenCreatingADivision_thenCreateANewDirector() {

    }
}