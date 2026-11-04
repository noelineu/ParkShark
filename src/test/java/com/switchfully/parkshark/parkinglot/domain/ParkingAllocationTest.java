package com.switchfully.parkshark.parkinglot.domain;

import com.switchfully.parkshark.exception.InactiveAllocationException;
import com.switchfully.parkshark.exception.OwnerAllocationException;
import com.switchfully.parkshark.member.domain.LicensePlate;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MembershipLevel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParkingAllocationTest {

    private final ParkingLot parkingLot = mock(ParkingLot.class);

    private Member memberWithId(Long id) {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(id);
        when(member.getMembershipLevel()).thenReturn(MembershipLevel.BRONZE);
        when(member.getLicensePlate()).thenReturn(new LicensePlate("1-ABC-123", "Belgium"));
        return member;
    }

    @Test
    void newAllocation_isActiveWithoutStopTime() {
        ParkingAllocation allocation = new ParkingAllocation(memberWithId(1L), parkingLot, "1-ABC-123");

        assertThat(allocation.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(allocation.getStopTime()).isNull();
    }

    @Test
    void stop_byTheOwner_thenIsStoppedWithAStopTime() {
        ParkingAllocation allocation = new ParkingAllocation(memberWithId(1L), parkingLot, "1-ABC-123");

        allocation.stop(1L);

        assertThat(allocation.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(allocation.getStopTime()).isNotNull();
        assertThat(allocation.getStopTime()).isAfterOrEqualTo(allocation.getStartTime());
    }

    @Test
    void stop_byAnotherMember_thenThrowsAndStaysActive() {
        ParkingAllocation allocation = new ParkingAllocation(memberWithId(1L), parkingLot, "1-ABC-123");

        assertThatThrownBy(() -> allocation.stop(2L))
                .isInstanceOf(OwnerAllocationException.class);

        assertThat(allocation.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(allocation.getStopTime()).isNull();
    }

    @Test
    void stop_twice_thenThrows() {
        ParkingAllocation allocation = new ParkingAllocation(memberWithId(1L), parkingLot, "1-ABC-123");
        allocation.stop(1L);

        assertThatThrownBy(() -> allocation.stop(1L))
                .isInstanceOf(InactiveAllocationException.class);
    }

    @Test
    void billedHours_areRoundedUp() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 4, 10, 0);

        assertThat(ParkingAllocation.billedHoursBetween(start, start.plusHours(2).plusMinutes(20))).isEqualTo(3);
        assertThat(ParkingAllocation.billedHoursBetween(start, start.plusHours(2))).isEqualTo(2);
        assertThat(ParkingAllocation.billedHoursBetween(start, start.plusHours(2).plusSeconds(1))).isEqualTo(3);
        assertThat(ParkingAllocation.billedHoursBetween(start, start.plusMinutes(59))).isEqualTo(1);
    }

    @Test
    void activeAllocation_cannotBeBilledNorInvoiced() {
        ParkingAllocation allocation = new ParkingAllocation(memberWithId(1L), parkingLot, "1-ABC-123");

        assertThatThrownBy(allocation::getBilledHours).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(allocation::markAsInvoiced).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void stoppedAllocation_canBeInvoicedOnlyOnce() {
        ParkingAllocation allocation = new ParkingAllocation(memberWithId(1L), parkingLot, "1-ABC-123");
        allocation.stop(1L);

        allocation.markAsInvoiced();

        assertThat(allocation.getStatus()).isEqualTo(Status.INVOICED);
        assertThatThrownBy(allocation::markAsInvoiced).isInstanceOf(IllegalStateException.class);
    }
}