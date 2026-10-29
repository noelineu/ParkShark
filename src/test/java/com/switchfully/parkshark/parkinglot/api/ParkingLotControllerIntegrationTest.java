package com.switchfully.parkshark.parkinglot.api;

import com.switchfully.parkshark.parkinglot.domain.ParkingLotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ParkingLotControllerIntegrationTest {

    private static final String UNREADABLE_BODY_MESSAGE =
            "The request body is not valid JSON or contains an unknown value";

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
    void createParkingLot_givenKnownZipWithAnotherLabel_thenKeepsTheLabelThatWasSent() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT.replace("\"zip\": \"8000\", \"label\": \"Brugge\"",
                                "\"zip\": \"1000\", \"label\": \"Brussel\"")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address.postalCode.zip").value("1000"))
                .andExpect(jsonPath("$.address.postalCode.label").value("Brussel"));
    }

    @Test
    void createParkingLot_givenUnknownCategory_thenReturns400() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT.replace("UNDERGROUND_BUILDING", "SUBMARINE")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(UNREADABLE_BODY_MESSAGE));
    }

    @Test
    void createParkingLot_givenUnknownDivision_thenReturns400() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PARKING_LOT.replace("\"divisionId\": 1", "\"divisionId\": 9999")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createParkingLot_givenMalformedJson_thenReturns400() throws Exception {
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(UNREADABLE_BODY_MESSAGE));
    }

    @Test
    void givenUnknownUrl_thenReturns404AndNot500() throws Exception {
        mockMvc.perform(get("/parking-lotz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllParkingLots_thenReturns200WithAllParkingLots() throws Exception {
        long expected = parkingLotRepository.count();

        mockMvc.perform(get("/parking-lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) expected));
    }

    @Test
    void getAllParkingLots_thenOnlyContainsTheRequiredFields() throws Exception {
        mockMvc.perform(get("/parking-lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].maxCapacity").exists())
                .andExpect(jsonPath("$[0].contactPersonEmail").exists())
                .andExpect(jsonPath("$[0].pricePerHour").doesNotExist())
                .andExpect(jsonPath("$[0].category").doesNotExist())
                .andExpect(jsonPath("$[0].address").doesNotExist())
                .andExpect(jsonPath("$[0].divisionId").doesNotExist())
                .andExpect(jsonPath("$[0].contactPerson").doesNotExist());
    }

    @Test
    void getAllParkingLots_givenContactPersonWithOnlyMobile_thenTelephoneIsNull() throws Exception {
        String guillemins = "$[?(@.name == 'Guillemins Tower')]";

        mockMvc.perform(get("/parking-lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(guillemins + ".contactPersonEmail").value("marie.dubois@parkshark.be"))
                .andExpect(jsonPath(guillemins + ".contactPersonTelephone").value(contains(nullValue())));
    }
}
