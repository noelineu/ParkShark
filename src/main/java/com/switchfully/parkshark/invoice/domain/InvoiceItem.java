package com.switchfully.parkshark.invoice.domain;

import com.switchfully.parkshark.member.domain.MembershipLevel;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocation;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "invoice_item")
public class InvoiceItem {

    static final BigDecimal FINE_PER_EXTRA_HOUR = new BigDecimal("2.50");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @OneToOne(optional = false)
    @JoinColumn(name = "parking_allocation_id")
    private ParkingAllocation parkingAllocation;

    @Column(name = "billed_hours")
    private int billedHours;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "fine")
    private BigDecimal fine;

    protected InvoiceItem() {
    }

    InvoiceItem(Invoice invoice, ParkingAllocation parkingAllocation, MembershipLevel level) {
        long hours = parkingAllocation.getBilledHours();
        this.invoice = invoice;
        this.parkingAllocation = parkingAllocation;
        this.billedHours = Math.toIntExact(hours);
        this.price = calculatePrice(hours, parkingAllocation.getParkingLot().getPricePerHour(), level);
        this.fine = calculateFine(hours, level);
    }

    public static BigDecimal calculatePrice(long hours, BigDecimal pricePerHour, MembershipLevel level) {
        BigDecimal partToPay = BigDecimal.valueOf(100 - level.getPricePerHourReduction())
                .divide(BigDecimal.valueOf(100));
        return pricePerHour
                .multiply(BigDecimal.valueOf(hours))
                .multiply(partToPay)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateFine(long hours, MembershipLevel level) {
        long extraHours = Math.max(0, hours - level.getMaxAllocationTime());
        return FINE_PER_EXTRA_HOUR
                .multiply(BigDecimal.valueOf(extraHours))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return price.add(fine);
    }

    public Long getId() { return id; }
    public ParkingAllocation getParkingAllocation() { return parkingAllocation; }
    public int getBilledHours() { return billedHours; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getFine() { return fine; }

}
