package com.switchfully.parkshark.address.domain;

import jakarta.persistence.*;

@Embeddable
public class Address {

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "street_number")
    private String streetNumber;

    @Embedded
    private PostalCode postalCode;

    protected Address() {
    }

    public Address(String streetName, String streetNumber, PostalCode postalCode) {
        if (streetName == null || streetName.isBlank()) {
            throw new IllegalArgumentException("An address needs a street name");
        }
        if (streetNumber == null || streetNumber.isBlank()) {
            throw new IllegalArgumentException("An address needs a street number");
        }
        if (postalCode == null) {
            throw new IllegalArgumentException("An address needs a postal code");
        }
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.postalCode = postalCode;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public PostalCode getPostalCode() {
        return postalCode;
    }
}
