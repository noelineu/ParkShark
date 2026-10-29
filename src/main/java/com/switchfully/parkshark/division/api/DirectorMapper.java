package com.switchfully.parkshark.division.api;

import com.switchfully.parkshark.division.api.dto.DirectorDto;
import com.switchfully.parkshark.division.domain.Director;
import org.springframework.stereotype.Component;

@Component
public class DirectorMapper {

    public DirectorDto toDto(Director director) {
        return new DirectorDto(
                director.getFirstName(),
                director.getLastName()
        );
    }

    public Director toDomain(DirectorDto dto) {
        return new Director(
                dto.firstName(),
                dto.lastName()
        );
    }
}
