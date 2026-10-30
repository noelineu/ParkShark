package com.switchfully.parkshark.member.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "LICENSE_PLATE")
public class LicensePlate {

    @Id
    @Column(name = "PLATE_NUMBER")
    private String plateNumber;

    @Column(name = "COUNTRY")
    private String country;

    public LicensePlate() {
    }

    public LicensePlate(String plateNumber, String country) {
        this.plateNumber = plateNumber;
        this.country = country;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getCountry() {
        return country;
    }
}
