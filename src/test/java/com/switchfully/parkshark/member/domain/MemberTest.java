package com.switchfully.parkshark.member.domain;

import com.switchfully.parkshark.address.domain.Address;
import com.switchfully.parkshark.address.domain.PostalCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    private final Address address = new Address("Tiensestraat", "12", new PostalCode("3000", "Leuven"));
    private final LicensePlate plate = new LicensePlate("1-ABC-123", "Belgium");

    @Test
    void givenValidData_thenMemberIsCreatedWithTodayAndBronze() {
        Member member = new Member("Emma", "Willems", "0471111111", "emma@mail.be", plate, address);

        assertThat(member.getFirstName()).isEqualTo("Emma");
        assertThat(member.getLicensePlate()).isSameAs(plate);
        assertThat(member.getAddress()).isSameAs(address);
        assertThat(member.getRegistrationDate()).isEqualTo(LocalDate.now());
        assertThat(member.getMembershipLevel()).isEqualTo(MembershipLevel.BRONZE);
    }

    @Test
    void givenNoFirstName_thenThrowsException() {
        assertThatThrownBy(() -> new Member(" ", "Willems", "0471111111", "emma@mail.be", plate, address))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A member needs a first name");
    }

    @Test
    void givenNoLastName_thenThrowsException() {
        assertThatThrownBy(() -> new Member("Emma", null, "0471111111", "emma@mail.be", plate, address))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A member needs a last name");
    }

    @Test
    void givenNoTelephone_thenThrowsException() {
        assertThatThrownBy(() -> new Member("Emma", "Willems", "", "emma@mail.be", plate, address))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A member needs a telephone number");
    }

    @Test
    void givenNoEmail_thenThrowsException() {
        assertThatThrownBy(() -> new Member("Emma", "Willems", "0471111111", null, plate, address))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A member needs an email address");
    }

    @Test
    void givenNoLicensePlate_thenThrowsException() {
        assertThatThrownBy(() -> new Member("Emma", "Willems", "0471111111", "emma@mail.be", null, address))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A member needs a license plate");
    }

    @Test
    void givenNoAddress_thenThrowsException() {
        assertThatThrownBy(() -> new Member("Emma", "Willems", "0471111111", "emma@mail.be", plate, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A member needs an address");
    }
}