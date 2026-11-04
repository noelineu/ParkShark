package com.switchfully.parkshark.exception;

public class AllocationNotFoundException extends NotFoundException {
    public AllocationNotFoundException(Long id) {
        super("Allocation with id: " + id + " not found.");
    }
}
