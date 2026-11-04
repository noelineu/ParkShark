package com.switchfully.parkshark.member.api.dto;

import java.time.LocalDate;

public record MemberSummaryDto (
    Long id,
    String firstName,
    String lastName,
    String licensePlateNumber,
    String telephone,
    String email,
    LocalDate registrationDate) {
}
