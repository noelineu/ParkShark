package com.switchfully.parkshark.division.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDivisionDto(@NotBlank String name, @NotBlank String originalName, @NotNull DirectorDto directorDto, Integer parentId) {}