package com.switchfully.parkshark.exception;

public class OwnerAllocationException extends RuntimeException {
    public OwnerAllocationException(Long allocationId, Long memberId) {
        super("Member " + memberId + " is not the owner of parking allocation " + allocationId);
    }
}
