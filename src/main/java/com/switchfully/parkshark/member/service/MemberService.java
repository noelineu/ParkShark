package com.switchfully.parkshark.member.service;

import com.switchfully.parkshark.member.api.MemberMapper;
import com.switchfully.parkshark.member.api.dto.CreateMemberDto;
import com.switchfully.parkshark.member.api.dto.MemberDto;
import com.switchfully.parkshark.member.domain.LicensePlate;
import com.switchfully.parkshark.member.domain.LicensePlateRepository;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;
    private final LicensePlateRepository licensePlateRepository;
    private final MemberMapper memberMapper;

    public MemberService(MemberRepository memberRepository, LicensePlateRepository licensePlateRepository,
                         MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.licensePlateRepository = licensePlateRepository;
        this.memberMapper = memberMapper;
    }

    @Transactional
    public MemberDto createMember(CreateMemberDto dto) {
        if (memberRepository.existsByLicensePlate_PlateNumber(dto.licensePlate().plateNumber())) {
            throw new IllegalArgumentException("A member with that license plate number already exists");
        }

        LicensePlate licensePlate = licensePlateRepository.save(
                new LicensePlate(dto.licensePlate().plateNumber(), dto.licensePlate().country()));

        Member member = memberMapper.toDomain(dto, licensePlate);
        memberRepository.save(member);
        logger.info("Registered member {} with id {}", member.getEmail(), member.getId());
        return memberMapper.toDto(member);
    }
}