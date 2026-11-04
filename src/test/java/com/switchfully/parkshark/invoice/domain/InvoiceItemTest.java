package com.switchfully.parkshark.invoice.domain;

import com.switchfully.parkshark.member.domain.MembershipLevel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceItemTest {

    private static final BigDecimal LADEUZE_PRICE = new BigDecimal("2.50");

    @Test
    void bronze_paysFullPrice() {
        assertThat(InvoiceItem.calculatePrice(3, LADEUZE_PRICE, MembershipLevel.BRONZE))
                .isEqualByComparingTo("7.50");
    }

    @Test
    void silver_gets20PercentReduction() {
        assertThat(InvoiceItem.calculatePrice(3, LADEUZE_PRICE, MembershipLevel.SILVER))
                .isEqualByComparingTo("6.00");
    }

    @Test
    void gold_gets30PercentReduction() {
        assertThat(InvoiceItem.calculatePrice(1, LADEUZE_PRICE, MembershipLevel.GOLD))
                .isEqualByComparingTo("1.75");
    }

    @Test
    void withinMaximumTime_noFine() {
        assertThat(InvoiceItem.calculateFine(4, MembershipLevel.BRONZE)).isEqualByComparingTo("0");
    }

    @Test
    void aboveMaximumTime_finePerExtraHour() {
        assertThat(InvoiceItem.calculateFine(6, MembershipLevel.BRONZE)).isEqualByComparingTo("5.00");
    }

    @Test
    void gold_canPark24HoursWithoutFine() {
        assertThat(InvoiceItem.calculateFine(24, MembershipLevel.GOLD)).isEqualByComparingTo("0");
    }
}