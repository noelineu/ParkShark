package com.switchfully.parkshark.exception;

public class ParkingLotNotFoundException extends NotFoundException {
    public ParkingLotNotFoundException(Long id) {
        super("No parking lot found for id: " + id);
    }
}
