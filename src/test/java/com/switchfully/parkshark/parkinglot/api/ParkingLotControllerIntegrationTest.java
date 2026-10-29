package com.switchfully.parkshark.parkinglot.api;

import com.switchfully.parkshark.address.domain.PostalCodeRepository;
import com.switchfully.parkshark.parkinglot.domain.ParkingLotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ParkingLotControllerIntegrationTest {

    // Division 1 ('ParkShark North') is inserted by V2__add_data.sql
    private static final String VALID_PARKING_LOT = """
            {
              "name": "Shark Station",
              "category": "UNDERGROUND_BUILDING",
              "maxCapacity": 120,
              "pricePerHour": 3.50,
              "contactPerson": {
                "name": "Bruce Shark",
                "mobilePhone": "0470999999",
                "telephone": null,
                "email": "bruce@parkshark.be",
                "address": {
                  "streetName": "Diestsestraat",
                  "streetNumber": "42",
                  "postalCode": { "zip": "3000", "label": "Leuven" }
                }
              },
              "address": {
                "streetName": "Sharkstraat",
                "streetNumber": "7",
                "postalCode": { "zip": "8000", "label": "Brugge" }
              },
              "divisionId": 1
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private PostalCodeRepository postalCodeRepository;

    @Test
    void createParkingLot_givenValidData_thenReturns201WithCreatedParkingLot() throws Exception {
        long parkingLotsBefore = parkingLotRepository.count();

        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Shark Station"))
                .andExpect(jsonPath("$.category").value("UNDERGROUND_BUILDING"))
                .andExpect(jsonPath("$.maxCapacity").value(120))
                .andExpect(jsonPath("$.pricePerHour").value(3.50))
                .andExpect(jsonPath("$.contactPerson.email").value("bruce@parkshark.be"))
                .andExpect(jsonPath("$.contactPerson.address.postalCode.label").value("Leuven"))
                .andExpect(jsonPath("$.address.streetName").value("Sharkstraat"))
                .andExpect(jsonPath("$.address.streetNumber").value("7"))
                .andExpect(jsonPath("$.divisionId").value(1));

        assertThat(parkingLotRepository.count()).isEqualTo(parkingLotsBefore + 1);
    }

    @Test
    void createParkingLot_givenUnknownPostalCode_thenPostalCodeIsCreated() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT))
                .andExpect(status().isCreated());

        assertThat(postalCodeRepository.findById("8000"))
                .hasValueSatisfying(postalCode -> assertThat(postalCode.getLabel()).isEqualTo("Brugge"));
    }

    @Test
    void createParkingLot_givenContactPersonWithoutAnyPhoneNumber_thenReturns400() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT.replace("\"0470999999\"", "null")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "A contact person needs a mobile phone number or a telephone number"));
    }

    @Test
    void createParkingLot_givenInvalidEmail_thenReturns400() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT.replace("bruce@parkshark.be", "not-an-email")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createParkingLot_givenMissingName_thenReturns400() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT.replace("\"Shark Station\"", "\"\"")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createParkingLot_givenUnknownCategory_thenReturns400() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT.replace("UNDERGROUND_BUILDING", "SUBMARINE")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createParkingLot_givenUnknownDivision_thenReturns400() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT.replace("\"divisionId\": 1", "\"divisionId\": 9999")))
                .andExpect(status().isBadRequest());
    }
}
