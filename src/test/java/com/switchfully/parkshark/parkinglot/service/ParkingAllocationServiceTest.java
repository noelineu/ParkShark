package com.switchfully.parkshark.parkinglot.service;

import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocation;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocationRepository;
import com.switchfully.parkshark.parkinglot.domain.ParkingLotRepository;
import com.switchfully.parkshark.parkinglot.domain.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        ParkingAllocationDto januaryDto = new ParkingAllocationDto(1L, null, null, null, null, null);
        ParkingAllocationDto marchDto = new ParkingAllocationDto(2L, null, null, null, null, null);
        when(parkingAllocationMapper.toDto(january)).thenReturn(januaryDto);
        when(parkingAllocationMapper.toDto(march)).thenReturn(marchDto);

        when(parkingAllocationRepository.findAll()).thenReturn(List.of(march, january));

        List<ParkingAllocationDto> result =
                parkingAllocationService.getAllParkingAllocations(null, null, Sort.Direction.ASC);

        assertEquals(List.of(januaryDto, marchDto), result);
    }
}