package com.switchfully.parkshark.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    boolean existsByLicensePlate_PlateNumber(String licensePlateNumber);
}
