package com.switchfully.parkshark.member.api.dto;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.member.domain.MembershipLevel;

import java.time.LocalDate;

public record MemberDto(
        int id,
        String firstName,
        String lastName,
        String telephone,
        String email,
        LicensePlateDto licensePlate,
        LocalDate registrationDate,
        MembershipLevel membershipLevel,
        AddressDto address) {
}