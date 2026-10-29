package com.switchfully.parkshark.division.api;

import com.switchfully.parkshark.division.api.dto.DirectorDto;
import com.switchfully.parkshark.division.domain.Director;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectorMapperTest {

    private DirectorMapper directorMapper;

    @BeforeEach
    void setUp() {
        directorMapper = new DirectorMapper();
    }

    @Test
    void givenADirector_whenMappingToDto_thenReturnDirectorDto() {
        Director director = new Director("Noéline", "LaBourreau");

        DirectorDto result = directorMapper.toDto(director);

        assertNotNull(result);
        assertEquals("Noéline", result.firstName());
        assertEquals("LaBourreau", result.lastName());
    }

    @Test
    void givenADirectorDto_whenMappingToDomain_thenReturnDirector() {
        DirectorDto dto = new DirectorDto("Noéline", "LaBourreau");

        Director result = directorMapper.toDomain(dto);

        assertNotNull(result);
        assertEquals("Noéline", result.getFirstName());
        assertEquals("LaBourreau", result.getLastName());
    }
}
