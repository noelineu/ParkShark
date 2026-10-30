package com.switchfully.parkshark.member.api.dto;

import com.switchfully.parkshark.member.domain.LicensePlate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicensePlateMapperTest {

    private final LicensePlateMapper mapper = new LicensePlateMapper();

    @Test
    void toDomain_keepsNumberAndCountry() {
        LicensePlate plate = mapper.toDomain(new LicensePlateDto("1-SPI-001", "Belgium"));

        assertThat(plate.getPlateNumber()).isEqualTo("1-SPI-001");
        assertThat(plate.getCountry()).isEqualTo("Belgium");
    }

    @Test
    void toDto_keepsNumberAndCountry() {
        LicensePlateDto dto = mapper.toDto(new LicensePlate("AB-123-CD", "France"));

        assertThat(dto.plateNumber()).isEqualTo("AB-123-CD");
        assertThat(dto.country()).isEqualTo("France");
    }
}