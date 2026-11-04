package com.switchfully.parkshark.invoice.domain;

import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MembershipLevel;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocation;
import com.switchfully.parkshark.parkinglot.domain.ParkingLot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InvoiceTest {

    private Member memberWithLevel(MembershipLevel level) {
        Member member = mock(Member.class);
        when(member.getMembershipLevel()).thenReturn(level);
        return member;
    }

    private ParkingAllocation stoppedAllocation(long billedHours, String pricePerHour) {
        ParkingLot parkingLot = mock(ParkingLot.class);
        when(parkingLot.getPricePerHour()).thenReturn(new BigDecimal(pricePerHour));
        ParkingAllocation allocation = mock(ParkingAllocation.class);
        when(allocation.getBilledHours()).thenReturn(billedHours);
        when(allocation.getParkingLot()).thenReturn(parkingLot);
        return allocation;
    }

    @Test
    void newInvoice_isOpenAndExpiresIn30Days() {
        Invoice invoice = new Invoice(memberWithLevel(MembershipLevel.BRONZE));

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.OPEN);
        assertThat(invoice.getReference()).startsWith("INV-");
        assertThat(invoice.getCreationDate()).isEqualTo(LocalDate.now());
        assertThat(invoice.getExpirationDate()).isEqualTo(LocalDate.now().plusDays(30));
        assertThat(invoice.getPaymentDate()).isNull();
    }

    @Test
    void twoInvoices_haveDifferentReferences() {
        Member member = memberWithLevel(MembershipLevel.BRONZE);

        assertThat(new Invoice(member).getReference()).isNotEqualTo(new Invoice(member).getReference());
    }

    @Test
    void addItem_marksTheAllocationAsInvoiced() {
        Invoice invoice = new Invoice(memberWithLevel(MembershipLevel.BRONZE));
        ParkingAllocation allocation = stoppedAllocation(3, "2.50");

        invoice.addItem(allocation);

        assertThat(invoice.getItems()).hasSize(1);
        verify(allocation).markAsInvoiced();
    }

    @Test
    void totalPrice_isMonthlyCostPlusEveryLine() {
        Invoice invoice = new Invoice(memberWithLevel(MembershipLevel.SILVER));
        invoice.addItem(stoppedAllocation(3, "2.50"));

        assertThat(invoice.getTotalPrice()).isEqualByComparingTo("16.00");
    }

    @Test
    void withoutAllocations_totalPriceIsTheMonthlyCost() {
        Invoice invoice = new Invoice(memberWithLevel(MembershipLevel.GOLD));

        assertThat(invoice.getItems()).isEmpty();
        assertThat(invoice.getTotalPrice()).isEqualByComparingTo("40");
    }
}