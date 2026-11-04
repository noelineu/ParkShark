package com.switchfully.parkshark.member.domain;

import java.math.BigDecimal;

public enum MembershipLevel {

    BRONZE(new BigDecimal("0"), 0, 4),
    SILVER(new BigDecimal("10"), 20 , 6),
    GOLD(new BigDecimal("40"), 30, 24);

    private final BigDecimal monthlyCost;
    private final int pricePerHourReduction;
    private final int maxAllocationTime;


    MembershipLevel(BigDecimal monthlyCost, int pricePerHourReduction, int maxAllocationTime) {
        this.monthlyCost = monthlyCost;
        this.pricePerHourReduction = pricePerHourReduction;
        this.maxAllocationTime = maxAllocationTime;
    }


    public BigDecimal getMonthlyCost() {
        return monthlyCost;
    }

    public int getPricePerHourReduction() {
        return pricePerHourReduction;
    }

    public int getMaxAllocationTime() {
        return maxAllocationTime;
    }
}
