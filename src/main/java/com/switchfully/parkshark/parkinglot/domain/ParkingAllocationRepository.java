package com.switchfully.parkshark.parkinglot.domain;

import com.switchfully.parkshark.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingAllocationRepository extends JpaRepository<ParkingAllocation, Long> {

    int countParkingAllocationByParkingLotAndStatus(ParkingLot parkingLot, Status status);

    List<ParkingAllocation> findParkingAllocationsByStatus(Status status);

    List<ParkingAllocation> findParkingAllocationsByMember(Member member);

    List<ParkingAllocation> findParkingAllocationsByMemberAndStatus(Member member, Status status);

}
