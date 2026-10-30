package com.switchfully.parkshark.member.api.dto;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.member.domain.MembershipLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MemberDto(int id, String firstName, String lastName, String telephone,
                        String email, LicensePlateDto licensePlateDto, LocalDate registrationDate, MembershipLevel membershipLevel,
                        AddressDto addressDto) {
}
