package com.switchfully.parkshark.member.service;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.address.api.dto.PostalCodeDto;
import com.switchfully.parkshark.exception.MemberNotFoundException;
import com.switchfully.parkshark.member.api.MemberMapper;
import com.switchfully.parkshark.member.api.dto.CreateMemberDto;
import com.switchfully.parkshark.member.api.dto.LicensePlateDto;
import com.switchfully.parkshark.member.api.dto.MemberDto;
import com.switchfully.parkshark.member.api.dto.MemberSummaryDto;
import com.switchfully.parkshark.member.domain.LicensePlate;
import com.switchfully.parkshark.member.domain.LicensePlateRepository;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LicensePlateRepository licensePlateRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    private final CreateMemberDto dto = new CreateMemberDto(
            "Peter", "Parker", "0475555555", "peter.parker@mail.be",
            new LicensePlateDto("1-SPI-001", "Belgium"),
            null,
            new AddressDto("Naamsestraat", "22", new PostalCodeDto("3000", "Leuven")));

    @Test
    void givenPlateAlreadyUsedByAMember_thenThrowsAndSavesNothing() {
        when(memberRepository.existsByLicensePlate_PlateNumber("1-SPI-001")).thenReturn(true);

        assertThatThrownBy(() -> memberService.createMember(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A member with that license plate number already exists");

        verify(licensePlateRepository, never()).save(any());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void givenFreePlate_thenSavesThePlateBeforeTheMemberAndReturnsTheDto() {
        Member member = mock(Member.class);
        MemberDto expectedDto = mock(MemberDto.class);

        when(memberRepository.existsByLicensePlate_PlateNumber("1-SPI-001")).thenReturn(false);
        when(licensePlateRepository.save(any(LicensePlate.class))).thenAnswer(call -> call.getArgument(0));
        when(memberMapper.toDomain(eq(dto), any(LicensePlate.class))).thenReturn(member);
        when(memberMapper.toDto(member)).thenReturn(expectedDto);

        MemberDto result = memberService.createMember(dto);

        assertThat(result).isSameAs(expectedDto);

        ArgumentCaptor<LicensePlate> savedPlate = ArgumentCaptor.forClass(LicensePlate.class);
        InOrder inOrder = inOrder(licensePlateRepository, memberRepository);
        inOrder.verify(licensePlateRepository).save(savedPlate.capture());
        inOrder.verify(memberRepository).save(member);

        assertThat(savedPlate.getValue().getPlateNumber()).isEqualTo("1-SPI-001");
        assertThat(savedPlate.getValue().getCountry()).isEqualTo("Belgium");
    }

    @Test
    void getAllMembers_returnsTheSummariesFromTheMapper() {
        List<Member> members = List.of(mock(Member.class), mock(Member.class));
        List<MemberSummaryDto> summaries = List.of(mock(MemberSummaryDto.class), mock(MemberSummaryDto.class));

        when(memberRepository.findAll()).thenReturn(members);
        when(memberMapper.toSummaryDto(members)).thenReturn(summaries);

        assertThat(memberService.getAllMembers()).isSameAs(summaries);
    }

    @Test
    void getAllMembers_givenNoMembers_thenReturnsEmptyList() {
        when(memberRepository.findAll()).thenReturn(List.of());

        assertThat(memberService.getAllMembers()).isEmpty();
    }

    @Test
    void getMember_givenExistingId_thenReturnsTheFullDto() {
        Member member = mock(Member.class);
        MemberDto expectedDto = mock(MemberDto.class);

        when(memberRepository.findById(1)).thenReturn(Optional.of(member));
        when(memberMapper.toDto(member)).thenReturn(expectedDto);

        assertThat(memberService.getMember(1)).isSameAs(expectedDto);
    }

    @Test
    void getMember_givenUnknownId_thenThrowsMemberNotFound() {
        when(memberRepository.findById(999999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMember(999999))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("No member found for id: 999999");

        verify(memberMapper, never()).toDto(any());
    }
}