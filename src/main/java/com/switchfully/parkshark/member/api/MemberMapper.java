package com.switchfully.parkshark.member.api;

import com.switchfully.parkshark.address.api.AddressMapper;
import com.switchfully.parkshark.member.api.dto.CreateMemberDto;
import com.switchfully.parkshark.member.api.dto.LicensePlateDto;
import com.switchfully.parkshark.member.api.dto.LicensePlateMapper;
import com.switchfully.parkshark.member.api.dto.MemberDto;
import com.switchfully.parkshark.member.domain.LicensePlate;
import com.switchfully.parkshark.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    private LicensePlateMapper licensePlateMapper;
    private AddressMapper addressMapper;

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
                addressMapper.toDomain(dto.addressDto()));
    }

    public MemberDto toDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getTelephone(),
                member.getEmail(),
                new LicensePlateDto(member.getLicensePlate().getPlateNumber(), member.getLicensePlate().getCountry()),
                member.getRegistrationDate(),
                member.getMembershipLevel(),
                addressMapper.toDto(member.getAddress()));
    }
}
