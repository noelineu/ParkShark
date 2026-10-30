package com.switchfully.parkshark.member.api;

import com.switchfully.parkshark.address.api.AddressMapper;
import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.address.api.dto.PostalCodeDto;
import com.switchfully.parkshark.member.api.dto.CreateMemberDto;
import com.switchfully.parkshark.member.api.dto.LicensePlateDto;
import com.switchfully.parkshark.member.api.dto.LicensePlateMapper;
import com.switchfully.parkshark.member.api.dto.MemberDto;
import com.switchfully.parkshark.member.domain.LicensePlate;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MembershipLevel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MemberMapperTest {

    private final MemberMapper mapper = new MemberMapper(new LicensePlateMapper(), new AddressMapper());

    private final CreateMemberDto createDto = new CreateMemberDto(
            "Peter", "Parker", "0475555555", "peter.parker@mail.be",
            new LicensePlateDto("1-SPI-001", "Belgium"),
            null,
            new AddressDto("Naamsestraat", "22", new PostalCodeDto("3000", "Leuven")));

    @Test
    void toDomain_usesTheGivenLicensePlateAndMapsTheAddress() {
        LicensePlate plate = new LicensePlate("1-SPI-001", "Belgium");

        Member member = mapper.toDomain(createDto, plate);

        assertThat(member.getFirstName()).isEqualTo("Peter");
        assertThat(member.getLastName()).isEqualTo("Parker");
        assertThat(member.getTelephone()).isEqualTo("0475555555");
        assertThat(member.getEmail()).isEqualTo("peter.parker@mail.be");
        assertThat(member.getLicensePlate()).isSameAs(plate);
        assertThat(member.getAddress().getStreetName()).isEqualTo("Naamsestraat");
        assertThat(member.getAddress().getPostalCode().getLabel()).isEqualTo("Leuven");
    }

    @Test
    void toDomain_keepsTheChosenMembershipLevel() {
        CreateMemberDto goldDto = new CreateMemberDto(
                "Peter", "Parker", "0475555555", "peter.parker@mail.be",
                new LicensePlateDto("1-SPI-001", "Belgium"),
                MembershipLevel.GOLD,
                new AddressDto("Naamsestraat", "22", new PostalCodeDto("3000", "Leuven")));

        Member member = mapper.toDomain(goldDto, new LicensePlate("1-SPI-001", "Belgium"));

        assertThat(member.getMembershipLevel()).isEqualTo(MembershipLevel.GOLD);
    }

    @Test
    void toDto_mapsEveryField() {
        Member member = mapper.toDomain(createDto, new LicensePlate("1-SPI-001", "Belgium"));

        MemberDto dto = mapper.toDto(member);

        assertThat(dto.firstName()).isEqualTo("Peter");
        assertThat(dto.lastName()).isEqualTo("Parker");
        assertThat(dto.telephone()).isEqualTo("0475555555");
        assertThat(dto.email()).isEqualTo("peter.parker@mail.be");
        assertThat(dto.licensePlate().plateNumber()).isEqualTo("1-SPI-001");
        assertThat(dto.licensePlate().country()).isEqualTo("Belgium");
        assertThat(dto.registrationDate()).isEqualTo(LocalDate.now());
        assertThat(dto.membershipLevel()).isEqualTo(MembershipLevel.BRONZE);
        assertThat(dto.address().streetNumber()).isEqualTo("22");
        assertThat(dto.address().postalCode().zip()).isEqualTo("3000");
    }
}