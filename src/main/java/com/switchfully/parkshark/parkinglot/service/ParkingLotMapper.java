package com.switchfully.parkshark.parkinglot.service;

import com.switchfully.parkshark.address.api.AddressMapper;
import com.switchfully.parkshark.address.service.AddressMapper;
import com.switchfully.parkshark.division.api.DivisionMapper;
import com.switchfully.parkshark.division.domain.Division;
import com.switchfully.parkshark.parkinglot.api.dto.ContactPersonDto;
import com.switchfully.parkshark.parkinglot.api.dto.CreateParkingLotDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingLotDto;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingLotSummaryDto;
import com.switchfully.parkshark.parkinglot.domain.ContactPerson;
import com.switchfully.parkshark.parkinglot.domain.ParkingLot;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotMapper {

    private final AddressMapper addressMapper;
    private final DivisionMapper divisionMapper;

    public ParkingLotMapper(AddressMapper addressMapper, DivisionMapper divisionMapper) {
        this.addressMapper = addressMapper;
        this.divisionMapper = divisionMapper;
    }

    public ParkingLot toDomain(CreateParkingLotDto dto, Division division) {
        return new ParkingLot(
                dto.name(),
                dto.category(),
                dto.maxCapacity(),
                dto.pricePerHour(),
                toDomain(dto.contactPerson()),
                addressMapper.toDomain(dto.address()),
                division);
    }

    public ParkingLotDto toDto(ParkingLot parkingLot) {
        return new ParkingLotDto(
                parkingLot.getId(),
                parkingLot.getName(),
                parkingLot.getCategory(),
                parkingLot.getMaxCapacity(),
                parkingLot.getPricePerHour(),
                toDto(parkingLot.getContactPerson()),
                addressMapper.toDto(parkingLot.getAddress()),
                divisionMapper.toDto(parkingLot.getDivision()));
    }

    private ContactPerson toDomain(ContactPersonDto dto) {
        return new ContactPerson(
                dto.name(),
                dto.mobilePhone(),
                dto.telephone(),
                dto.email(),
                addressMapper.toDomain(dto.address()));
    }

    private ContactPersonDto toDto(ContactPerson contactPerson) {
        return new ContactPersonDto(
                contactPerson.getName(),
                contactPerson.getMobilePhone(),
                contactPerson.getTelephone(),
                contactPerson.getEmail(),
                addressMapper.toDto(contactPerson.getAddress()));
    }

    public ParkingLotSummaryDto toSummaryDto(ParkingLot parkingLot) {
        return new ParkingLotSummaryDto(
                parkingLot.getId(),
                parkingLot.getName(),
                parkingLot.getMaxCapacity(),
                parkingLot.getContactPerson().getEmail(),
                parkingLot.getContactPerson().getTelephone());
    }
}
