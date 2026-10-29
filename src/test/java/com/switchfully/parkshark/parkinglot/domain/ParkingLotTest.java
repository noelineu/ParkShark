package com.switchfully.parkshark.parkinglot.domain;

import com.switchfully.parkshark.address.domain.Address;
import com.switchfully.parkshark.address.domain.PostalCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParkingLotTest {

    private final Address address = new Address("Ladeuzeplein", "1", new PostalCode("3000", "Leuven"));
    private final ContactPerson contactPerson =
            new ContactPerson("Jan", "0470123456", null, "jan@parkshark.be", address);

    @Test
    void givenValidData_thenParkingLotIsCreated() {
        ParkingLot parkingLot = new ParkingLot("Ladeuze", Category.UNDERGROUND_BUILDING, 250,
                new BigDecimal("2.50"), contactPerson, address, 1L);

        assertThat(parkingLot.getName()).isEqualTo("Ladeuze");
        assertThat(parkingLot.getCategory()).isEqualTo(Category.UNDERGROUND_BUILDING);
        assertThat(parkingLot.getMaxCapacity()).isEqualTo(250);
        assertThat(parkingLot.getDivisionId()).isEqualTo(1L);
    }

    @Test
    void givenZeroCapacity_thenThrowsException() {
        assertThatThrownBy(() -> new ParkingLot("Ladeuze", Category.UNDERGROUND_BUILDING, 0,
                new BigDecimal("2.50"), contactPerson, address, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNegativePrice_thenThrowsException() {
        assertThatThrownBy(() -> new ParkingLot("Ladeuze", Category.UNDERGROUND_BUILDING, 250,
                new BigDecimal("-1"), contactPerson, address, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNoDivision_thenThrowsException() {
        assertThatThrownBy(() -> new ParkingLot("Ladeuze", Category.UNDERGROUND_BUILDING, 250,
                new BigDecimal("2.50"), contactPerson, address, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
