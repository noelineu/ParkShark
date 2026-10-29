package com.switchfully.parkshark.division.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "DIRECTOR")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    public Director(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("A director needs a first name");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("A director needs a last name");
        }
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Director() {
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
}
