package com.switchfully.parkshark.parkinglot.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingAllocationRepository extends JpaRepository<ParkingAllocation, Long> {

    int countParkingAllocationByParkingLotAndStatus(ParkingLot parkingLot, Status status);

    List<ParkingAllocation> findParkingAllocationsByStatus(Status status);

    List<ParkingAllocation> findParkingAllocationsByParkingLotAndStatus(ParkingLot parkingLot, Status status);

    List<ParkingAllocation> findParkingAllocationsByParkingLot(ParkingLot parkingLot);
}
