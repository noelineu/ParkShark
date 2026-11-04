package com.switchfully.parkshark.parkinglot.domain;
import com.switchfully.parkshark.exception.InactiveAllocationException;
import com.switchfully.parkshark.exception.OwnerAllocationException;
import com.switchfully.parkshark.member.domain.Member;
import com.switchfully.parkshark.member.domain.MembershipLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PARKING_ALLOCATION")
public class ParkingAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PARKING_LOT_ID")
    private ParkingLot parkingLot;

    @Column(name = "LICENSE_PLATE_NUMBER")
    private String licensePlateNumber;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "STOP_TIME")
    private LocalDateTime stopTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    public ParkingAllocation() {

    }

    public ParkingAllocation(Member member, ParkingLot parkingLot, String licensePlateNumber) {
        if (member == null || parkingLot == null || licensePlateNumber == null) {
            throw new IllegalArgumentException("An allocation needs a member, parking lot and license plate.");
        }

        if (!(member.getMembershipLevel() == MembershipLevel.GOLD || licensePlateNumber.equals(member.getLicensePlate().getPlateNumber()))) {
            throw new IllegalArgumentException("The member's plate number and the allocation's plate number are different.");
        }

        this.member = member;
        this.parkingLot = parkingLot;
        this.licensePlateNumber = licensePlateNumber;
        this.startTime = LocalDateTime.now();
        this.stopTime = null;
        this.status = Status.ACTIVE;
    }

    public void stop(Long id) {
        if (!getMember().getId().equals(id)) {
            throw new OwnerAllocationException(id, getMember().getId());
        }

        if (status != Status.ACTIVE) {
            throw new InactiveAllocationException(id);
        }

        this.stopTime = LocalDateTime.now();
        this.status = Status.INACTIVE;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public Status getStatus() {
        return status;
    }
}
