package com.switchfully.parkshark.address.domain;

import jakarta.persistence.*;

@Embeddable
public class PostalCode {

    @Column(name = "postal_code")
    private String zip;

    @Column(name = "postal_label")
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
