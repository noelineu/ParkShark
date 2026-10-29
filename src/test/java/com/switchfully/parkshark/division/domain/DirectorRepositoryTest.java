package com.switchfully.parkshark.division.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE)
class DirectorRepositoryTest {

    @Autowired
    private DirectorRepository directorRepository;

    @Test
    void findByFirstNameAndLastName() {
        Director director = directorRepository.findByFirstNameAndLastName("Tony", "Sharkano");

        assertThat(director.getFirstName()).isEqualTo("Tony");
        assertThat(director.getLastName()).isEqualTo("Sharkano");
    }

    @Test
    void findByFirstNameAndLastName_returnsNullIfDirectorDoesNotExist() {
        Director director = directorRepository.findByFirstNameAndLastName("Ton", "Sharkano");

        assertThat(director).isNull();
    }
}