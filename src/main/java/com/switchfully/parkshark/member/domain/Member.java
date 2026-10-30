package com.switchfully.parkshark.member.domain;

import com.switchfully.parkshark.address.domain.Address;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "TELEPHONE")
    private String telephone;

    @Column(name = "EMAIL")
    private String email;

    @OneToOne(optional = false)
    @JoinColumn(name = "LICENSE_PLATE_NUMBER")
    private LicensePlate licensePlate;

    @Column(name = "REGISTRATION_DATE")
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "MEMBERSHIP_LEVEL")
    private MembershipLevel membershipLevel;

    @Embedded
    private Address address;

    public Member() {
    }

    public Member(String firstName, String lastName, String telephone, String email, LicensePlate licensePlate, MembershipLevel membershipLevel, Address address) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("A member needs a first name");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("A member needs a last name");
        }
        if (telephone == null || telephone.isBlank()) {
            throw new IllegalArgumentException("A member needs a telephone number");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("A member needs an email address");
        }
        if (licensePlate == null) {
            throw new IllegalArgumentException("A member needs a license plate");
        }
        if (address == null) {
            throw new IllegalArgumentException("A member needs an address");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.telephone = telephone;
        this.email = email;
        this.licensePlate = licensePlate;
        this.registrationDate = LocalDate.now();
        this.membershipLevel = membershipLevel != null ? membershipLevel : MembershipLevel.BRONZE;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public LicensePlate getLicensePlate() {
        return licensePlate;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    public Address getAddress() {
        return address;
    }
}
