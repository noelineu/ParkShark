package com.switchfully.parkshark.parkinglot.domain;

import com.switchfully.parkshark.address.domain.Address;
import com.switchfully.parkshark.division.domain.Division;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "parking_lot")
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "category")
    private Category category;

    @Column(name = "max_capacity")
    private int maxCapacity;

    @Column(name = "price_per_hour")
    private BigDecimal pricePerHour;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "contact_person_id")
    private ContactPerson contactPerson;

    @Embedded
    private Address address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "division_id")
    private Division division;

    protected ParkingLot() {
    }

    public ParkingLot(String name, Category category, int maxCapacity, BigDecimal pricePerHour,
                      ContactPerson contactPerson, Address address, Division division) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A parking lot needs a name");
        }
        if (category == null) {
            throw new IllegalArgumentException("A parking lot needs a category");
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("A parking lot needs a positive max capacity");
        }
        if (pricePerHour == null || pricePerHour.signum() < 0) {
            throw new IllegalArgumentException("A parking lot needs a price per hour of at least 0");
        }
        if (contactPerson == null) {
            throw new IllegalArgumentException("A parking lot needs a contact person");
        }
        if (address == null) {
            throw new IllegalArgumentException("A parking lot needs an address");
        }
        if (division == null) {
            throw new IllegalArgumentException("A parking lot needs a division");
        }
        this.name = name;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.pricePerHour = pricePerHour;
        this.contactPerson = contactPerson;
        this.address = address;
        this.division = division;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public ContactPerson getContactPerson() {
        return contactPerson;
    }

    public Address getAddress() {
        return address;
    }

    public Division getDivision() {
        return division;
    }
}
