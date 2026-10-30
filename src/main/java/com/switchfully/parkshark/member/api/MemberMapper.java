package com.switchfully.parkshark.member.api;

import com.switchfully.parkshark.address.api.AddressMapper;
import com.switchfully.parkshark.member.api.dto.CreateMemberDto;
import com.switchfully.parkshark.member.api.dto.LicensePlateMapper;
import com.switchfully.parkshark.member.api.dto.MemberDto;
import com.switchfully.parkshark.member.api.dto.MemberSummaryDto;
import com.switchfully.parkshark.member.domain.LicensePlate;
import com.switchfully.parkshark.member.domain.Member;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberMapper {

    private final LicensePlateMapper licensePlateMapper;
    private final AddressMapper addressMapper;

    public MemberMapper(LicensePlateMapper licensePlateMapper, AddressMapper addressMapper) {
        this.licensePlateMapper = licensePlateMapper;
        this.addressMapper = addressMapper;
    }

    public Member toDomain(CreateMemberDto dto, LicensePlate licensePlate) {
        return new Member(
                dto.firstName(),
                dto.lastName(),
                dto.telephone(),
                dto.email(),
                licensePlate,
                addressMapper.toDomain(dto.address()));
    }

    public MemberDto toDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getTelephone(),
                member.getEmail(),
                licensePlateMapper.toDto(member.getLicensePlate()),
                member.getRegistrationDate(),
                member.getMembershipLevel(),
                addressMapper.toDto(member.getAddress()));
    }

    public MemberSummaryDto toSummaryDto(Member member) {
        return new MemberSummaryDto(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getLicensePlate().getPlateNumber(),
                member.getTelephone(),
                member.getEmail(),
                member.getRegistrationDate());
    }

    public List<MemberSummaryDto> toSummaryDto(List<Member> members) {
        return members.stream()
                .map(this::toSummaryDto)
                .toList();
    }
}