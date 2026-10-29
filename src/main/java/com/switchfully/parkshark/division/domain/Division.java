package com.switchfully.parkshark.division.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "DIVISION")
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ORIGINAL_NAME")
    private String original_name;

    @OneToOne
    @JoinColumn(name = "DIRECTOR_ID")
    private Director director;

    public Division(String name, String original_name, Director director) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A division needs a name");
        }
        if (original_name == null || original_name.isBlank()) {
            throw new IllegalArgumentException("A division needs an original name");
        }
        if (director == null) {
            throw new IllegalArgumentException("A division needs a director");
        }

        this.name = name;
        this.original_name = original_name;
        this.director = director;
    }

    public Division() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public Director getDirector() {
        return director;
    }
}
