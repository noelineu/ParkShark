package com.switchfully.parkshark.address.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zipcode")
public class PostalCode {

    @Id
    @Column(name = "zip")
    private String zip;

    @Column(name = "label")
    private String label;

    protected PostalCode() {
    }

    public PostalCode(String zip, String label) {
        if (zip == null || zip.isBlank()) {
            throw new IllegalArgumentException("A postal code needs a zip");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("A postal code needs a label");
        }
        this.zip = zip;
        this.label = label;
    }

    public String getZip() {
        return zip;
    }

    public String getLabel() {
        return label;
    }
}
