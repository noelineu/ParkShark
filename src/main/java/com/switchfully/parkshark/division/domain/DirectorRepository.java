package com.switchfully.parkshark.division.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Integer> {
    Director findByFirstNameAndLastName(String firstName, String lastName);

}
