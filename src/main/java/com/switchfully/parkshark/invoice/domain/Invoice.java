package com.switchfully.parkshark.invoice.domain;

import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocation;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoice")
public class Invoice {

    static final int DAYS_UNTIL_EXPIRATION = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference")
    private String reference;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status")
    private InvoiceStatus status;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "membership_cost")
    private BigDecimal membershipCost;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceItem> items = new ArrayList<>();

    protected Invoice() {
    }

    public Invoice(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("An invoice needs a member");
        }
        this.member = member;
        this.reference = "INV-" + UUID.randomUUID();
        this.creationDate = LocalDate.now();
        this.expirationDate = creationDate.plusDays(DAYS_UNTIL_EXPIRATION);
        this.status = InvoiceStatus.OPEN;
        this.membershipCost = member.getMembershipLevel().getMonthlyCost();
    }

    public void addItem(ParkingAllocation allocation) {
        items.add(new InvoiceItem(this, allocation, member.getMembershipLevel()));
        allocation.markAsInvoiced();
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(InvoiceItem::getTotal)
                .reduce(membershipCost, BigDecimal::add);
    }

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public Member getMember() { return member; }
    public LocalDate getCreationDate() { return creationDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public InvoiceStatus getStatus() { return status; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public BigDecimal getMembershipCost() { return membershipCost; }
    public List<InvoiceItem> getItems() { return Collections.unmodifiableList(items); }
}