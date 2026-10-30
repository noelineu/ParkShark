package com.switchfully.parkshark.division.api;

import com.switchfully.parkshark.division.api.dto.CreateDivisionDto;
import com.switchfully.parkshark.division.api.dto.DivisionDto;
import com.switchfully.parkshark.division.domain.Director;
import com.switchfully.parkshark.division.domain.Division;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DivisionMapper {
    private final DirectorMapper directorMapper;

    public DivisionMapper(DirectorMapper directorMapper) {
        this.directorMapper = directorMapper;
    }

    public DivisionDto toDto(Division division) {
        return new DivisionDto(
                division.getName(),
                division.getOriginal_name(),
                directorMapper.toDto(division.getDirector()),
                division.getParentId());
    }

    public List<DivisionDto> toDto(List<Division> divisions) {
        return divisions.stream()
                .map(this::toDto)
                .toList();
    }

    public Division toDomain(CreateDivisionDto dto, Director director) {
        return new Division(
                dto.name(),
                dto.originalName(),
                director,
                dto.parentId()
        );
    }
}
