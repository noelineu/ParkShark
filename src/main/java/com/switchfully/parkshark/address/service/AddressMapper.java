package com.switchfully.parkshark.address.service;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.address.api.dto.PostalCodeDto;
import com.switchfully.parkshark.address.domain.Address;
import com.switchfully.parkshark.address.domain.PostalCode;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toDomain(AddressDto dto) {
        return new Address(
                dto.streetName(),
                dto.streetNumber(),
                new PostalCode(dto.postalCode().zip(), dto.postalCode().label()));
    }

    public AddressDto toDto(Address address) {
        PostalCode postalCode = address.getPostalCode();
        return new AddressDto(
                address.getStreetName(),
                address.getStreetNumber(),
                new PostalCodeDto(postalCode.getZip(), postalCode.getLabel()));
    }
}