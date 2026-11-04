package com.switchfully.parkshark.exception;

public class InactiveAllocationException extends IllegalArgumentException {
    public InactiveAllocationException(Long id) {
        super("Allocation with id: " + id + " is inactive.");
    }
}
