package com.switchfully.parkshark.exception;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException(Long id) {
        super("No member found for id: " + id);
    }
}
