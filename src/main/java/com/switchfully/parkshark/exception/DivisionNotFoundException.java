package com.switchfully.parkshark.exception;

public class DivisionNotFoundException extends NotFoundException {
    public DivisionNotFoundException(Long id) {
        super("No division found for id: " + id);
    }
}
