package com.switchfully.parkshark.exception;

public class ParkingLotNotFoundException extends NotFoundException {
    public ParkingLotNotFoundException(Long id) {
        super("Parking lot with id " + id + " not found.");
    }
}
