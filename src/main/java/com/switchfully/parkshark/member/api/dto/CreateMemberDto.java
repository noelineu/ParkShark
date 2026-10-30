package com.switchfully.parkshark.member.api.dto;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.member.domain.MembershipLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMemberDto(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String telephone,
                              @NotBlank @Email String email, @NotNull @Valid LicensePlateDto licensePlateDto, @NotNull
                              MembershipLevel membershipLevel, @NotNull @Valid AddressDto addressDto) {
}
