package com.switchfully.parkshark.exception;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException(Integer id) {
        super("No member found for id: " + id);
    }
}
