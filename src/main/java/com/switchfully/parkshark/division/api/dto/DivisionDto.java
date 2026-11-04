package com.switchfully.parkshark.division.api.dto;

public record DivisionDto(Long id, String name, String originalName, DirectorDto directorDto, Long parentId) {}