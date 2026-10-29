package com.switchfully.parkshark.division.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDivisionDto(@NotBlank String name, @NotBlank String originalName, @NotBlank DirectorDto directorDto) {}