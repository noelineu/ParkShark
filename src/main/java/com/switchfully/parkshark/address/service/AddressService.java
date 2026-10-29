package com.switchfully.parkshark.address.service;

import com.switchfully.parkshark.address.api.dto.AddressDto;
import com.switchfully.parkshark.address.api.dto.PostalCodeDto;
import com.switchfully.parkshark.address.domain.Address;
import com.switchfully.parkshark.address.domain.PostalCode;
import com.switchfully.parkshark.address.domain.PostalCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

    private final PostalCodeRepository postalCodeRepository;

    public AddressService(PostalCodeRepository postalCodeRepository) {
        this.postalCodeRepository = postalCodeRepository;
    }

    /**
     * Postal codes are shared between addresses: an existing zip is reused, an unknown one is created.
     */
    @Transactional
    public Address toAddress(AddressDto dto) {
        PostalCodeDto postalCodeDto = dto.postalCode();
        PostalCode postalCode = postalCodeRepository.findById(postalCodeDto.zip())
                .orElseGet(() -> postalCodeRepository.save(new PostalCode(postalCodeDto.zip(), postalCodeDto.label())));
        return new Address(dto.streetName(), dto.streetNumber(), postalCode);
    }

    public AddressDto toDto(Address address) {
        PostalCode postalCode = address.getPostalCode();
        return new AddressDto(
                address.getStreetName(),
                address.getStreetNumber(),
                new PostalCodeDto(postalCode.getZip(), postalCode.getLabel()));
    }
}
