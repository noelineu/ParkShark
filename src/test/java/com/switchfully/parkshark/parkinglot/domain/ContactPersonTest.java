package com.switchfully.parkshark.parkinglot.domain;

import com.switchfully.parkshark.address.domain.Address;
import com.switchfully.parkshark.address.domain.PostalCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContactPersonTest {

    private final Address address = new Address("Bondgenotenlaan", "10", new PostalCode("3000", "Leuven"));

    @Test
    void givenOnlyAMobilePhone_thenContactPersonIsCreated() {
        ContactPerson contactPerson = new ContactPerson("Jan", "0470123456", null, "jan@parkshark.be", address);

        assertThat(contactPerson.getMobilePhone()).isEqualTo("0470123456");
        assertThat(contactPerson.getTelephone()).isNull();
    }

    @Test
    void givenOnlyATelephone_thenContactPersonIsCreated() {
        ContactPerson contactPerson = new ContactPerson("Jan", null, "016123456", "jan@parkshark.be", address);

        assertThat(contactPerson.getTelephone()).isEqualTo("016123456");
    }

    @Test
    void givenNoPhoneNumberAtAll_thenThrowsException() {
        assertThatThrownBy(() -> new ContactPerson("Jan", " ", null, "jan@parkshark.be", address))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mobile phone number or a telephone number");
    }

    @Test
    void givenNoEmail_thenThrowsException() {
        assertThatThrownBy(() -> new ContactPerson("Jan", "0470123456", null, null, address))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
