package com.switchfully.parkshark.parkinglot.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotAllocationRepository extends JpaRepository<ParkingAllocation, Integer> {

    int countParkingAllocationByParkingLotAndStatus(ParkingLot parkingLot, Status status);
}
