package com.switchfully.parkshark.member.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class MembershipLevelTest {
    @Test
    void bronze_isFreeWithoutReductionAndMax4Hours() {
        assertThat(MembershipLevel.BRONZE.getMonthlyCost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(MembershipLevel.BRONZE.getPricePerHourReduction()).isZero();
        assertThat(MembershipLevel.BRONZE.getMaxAllocationTime()).isEqualTo(4);
    }

    @Test
    void silver_costs10With20PercentReductionAndMax6Hours() {
        assertThat(MembershipLevel.SILVER.getMonthlyCost()).isEqualByComparingTo("10");
        assertThat(MembershipLevel.SILVER.getPricePerHourReduction()).isEqualTo(20);
        assertThat(MembershipLevel.SILVER.getMaxAllocationTime()).isEqualTo(6);
    }

    @Test
    void gold_costs40With30PercentReductionAndMax24Hours() {
        assertThat(MembershipLevel.GOLD.getMonthlyCost()).isEqualByComparingTo("40");
        assertThat(MembershipLevel.GOLD.getPricePerHourReduction()).isEqualTo(30);
        assertThat(MembershipLevel.GOLD.getMaxAllocationTime()).isEqualTo(24);
    }

}