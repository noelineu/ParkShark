package com.switchfully.parkshark.member.api.dto;

import com.switchfully.parkshark.member.domain.LicensePlate;
import org.springframework.stereotype.Component;

@Component
public class LicensePlateMapper {

    public LicensePlate toDomain(LicensePlateDto dto) {
        return new LicensePlate(
                dto.plateNumber(),
                dto.country());
    }

    public LicensePlateDto toDto(LicensePlate licensePlate) {
        return new LicensePlateDto(
                licensePlate.getPlateNumber(),
                licensePlate.getCountry());
    }
}
