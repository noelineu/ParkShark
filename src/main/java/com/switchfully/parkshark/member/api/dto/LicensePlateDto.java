package com.switchfully.parkshark.member.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LicensePlateDto(@NotBlank String plateNumber, @NotBlank String country) {
}
