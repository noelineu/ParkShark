package com.switchfully.parkshark.parkinglot.service;

import com.switchfully.parkshark.exception.MemberNotFoundException;
import com.switchfully.parkshark.exception.ParkingLotNotFoundException;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingAllocationServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ParkingAllocationRepository parkingAllocationRepository;
    @Mock
    private ParkingAllocationMapper parkingAllocationMapper;

    @InjectMocks
    private ParkingAllocationService parkingAllocationService;

    @Test
    void givenLimitZero_whenGettingAllocations_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> parkingAllocationService.getAllParkingAllocations(0, null, Sort.Direction.ASC));
    }

    @Test
    void givenNoStatus_whenGettingAllocations_thenSearchesAllAllocations() {
        when(parkingAllocationRepository.findAll()).thenReturn(List.of());

        parkingAllocationService.getAllParkingAllocations(null, null, Sort.Direction.ASC);

        verify(parkingAllocationRepository).findAll();
    }

    @Test
    void givenStatus_whenGettingAllocations_thenSearchesOnThatStatus() {
        when(parkingAllocationRepository.findParkingAllocationsByStatus(Status.INACTIVE)).thenReturn(List.of());

        parkingAllocationService.getAllParkingAllocations(null, Status.INACTIVE, Sort.Direction.ASC);

        verify(parkingAllocationRepository).findParkingAllocationsByStatus(Status.INACTIVE);
    }

    @Test
    void givenAscendingOrder_whenGettingAllocations_thenOldestComesFirst() {
        ParkingAllocation january = mock(ParkingAllocation.class);
        ParkingAllocation march = mock(ParkingAllocation.class);
        when(january.getStartTime()).thenReturn(LocalDateTime.of(2026, 1, 1, 8, 0));
        when(march.getStartTime()).thenReturn(LocalDateTime.of(2026, 3, 1, 8, 0));

        ParkingAllocationDto januaryDto = new ParkingAllocationDto(1L, null, null, null, null, null, null);
        ParkingAllocationDto marchDto = new ParkingAllocationDto(2L, null, null, null, null, null, null);
        when(parkingAllocationMapper.toDto(january)).thenReturn(januaryDto);
        when(parkingAllocationMapper.toDto(march)).thenReturn(marchDto);

        when(parkingAllocationRepository.findAll()).thenReturn(List.of(march, january));

        List<ParkingAllocationDto> result =
                parkingAllocationService.getAllParkingAllocations(null, null, Sort.Direction.ASC);

        assertEquals(List.of(januaryDto, marchDto), result);
    }

    @Test
    void givenUnknownMember_whenGettingAllocationsByMember_thenThrowsMemberNotFoundException() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class,
                () -> parkingAllocationService.getAllParkingLotsByMember(99L, null));

        verify(parkingAllocationRepository, never()).findParkingAllocationsByMember(any());
        verify(parkingAllocationRepository, never()).findParkingAllocationsByMemberAndStatus(any(), any());
    }

    @Test
    void givenNoStatus_whenGettingAllocationsByMember_thenSearchesAllAllocationsOfThatMember() {
        Member member = mock(Member.class);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(parkingAllocationRepository.findParkingAllocationsByMember(member)).thenReturn(List.of());

        parkingAllocationService.getAllParkingLotsByMember(1L, null);

        verify(parkingAllocationRepository).findParkingAllocationsByMember(member);
        verify(parkingAllocationRepository, never()).findParkingAllocationsByMemberAndStatus(any(), any());
    }

    @Test
    void givenStatus_whenGettingAllocationsByMember_thenSearchesOnMemberAndThatStatus() {
        Member member = mock(Member.class);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(parkingAllocationRepository.findParkingAllocationsByMemberAndStatus(member, Status.ACTIVE))
                .thenReturn(List.of());

        parkingAllocationService.getAllParkingLotsByMember(1L, Status.ACTIVE);

        verify(parkingAllocationRepository).findParkingAllocationsByMemberAndStatus(member, Status.ACTIVE);
        verify(parkingAllocationRepository, never()).findParkingAllocationsByMember(any());
    }

    @Test
    void givenMemberWithoutAllocations_whenGettingAllocationsByMember_thenReturnsEmptyList() {
        Member member = mock(Member.class);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(parkingAllocationRepository.findParkingAllocationsByMember(member)).thenReturn(List.of());

        List<ParkingAllocationDto> result = parkingAllocationService.getAllParkingLotsByMember(1L, null);

        assertEquals(List.of(), result);
    }

    @Test
    void givenUnknownParkingLot_whenGettingAllocationsByParkingLot_thenThrowsParkingLotNotFoundException() {
        when(parkingLotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ParkingLotNotFoundException.class,
                () -> parkingAllocationService.getAllParkingAllocationsByParkingLot(99L, null));

        verify(parkingAllocationRepository, never()).findParkingAllocationsByParkingLot(any());
        verify(parkingAllocationRepository, never()).findParkingAllocationsByParkingLotAndStatus(any(), any());
    }

    @Test
    void givenNoStatus_whenGettingAllocationsByParkingLot_thenSearchesAllAllocationsOfThatParkingLot() {
        ParkingLot parkingLot = mock(ParkingLot.class);
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(parkingAllocationRepository.findParkingAllocationsByParkingLot(parkingLot)).thenReturn(List.of());

        parkingAllocationService.getAllParkingAllocationsByParkingLot(1L, null);

        verify(parkingAllocationRepository).findParkingAllocationsByParkingLot(parkingLot);
        verify(parkingAllocationRepository, never()).findParkingAllocationsByParkingLotAndStatus(any(), any());
    }

    @Test
    void givenStatus_whenGettingAllocationsByParkingLot_thenSearchesOnParkingLotAndThatStatus() {
        ParkingLot parkingLot = mock(ParkingLot.class);
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(parkingAllocationRepository.findParkingAllocationsByParkingLotAndStatus(parkingLot, Status.ACTIVE))
                .thenReturn(List.of());

        parkingAllocationService.getAllParkingAllocationsByParkingLot(1L, Status.ACTIVE);

        verify(parkingAllocationRepository).findParkingAllocationsByParkingLotAndStatus(parkingLot, Status.ACTIVE);
        verify(parkingAllocationRepository, never()).findParkingAllocationsByParkingLot(any());
    }

    @Test
    void givenParkingLotWithoutAllocations_whenGettingAllocationsByParkingLot_thenReturnsEmptyList() {
        ParkingLot parkingLot = mock(ParkingLot.class);
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
        when(parkingAllocationRepository.findParkingAllocationsByParkingLot(parkingLot)).thenReturn(List.of());

        List<ParkingAllocationDto> result = parkingAllocationService.getAllParkingAllocationsByParkingLot(1L, null);

        assertEquals(List.of(), result);
    }
}