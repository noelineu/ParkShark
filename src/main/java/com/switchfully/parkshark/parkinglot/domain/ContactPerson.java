package com.switchfully.parkshark.parkinglot.domain;

import com.switchfully.parkshark.address.domain.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contact_person")
public class ContactPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Embedded
    private Address address;

    protected ContactPerson() {
    }

    public ContactPerson(String name, String mobilePhone, String telephone, String email, Address address) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A contact person needs a name");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("A contact person needs an email address");
        }
        if (isBlank(mobilePhone) && isBlank(telephone)) {
            throw new IllegalArgumentException("A contact person needs a mobile phone number or a telephone number");
        }
        if (address == null) {
            throw new IllegalArgumentException("A contact person needs an address");
        }
        this.name = name;
        this.mobilePhone = mobilePhone;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }
}
