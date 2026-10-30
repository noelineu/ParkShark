package com.switchfully.parkshark.exception;

public class DivisionNotFoundException extends NotFoundException {
    public DivisionNotFoundException(Integer id) {
        super("No division found for id: " + id);
    }
}
