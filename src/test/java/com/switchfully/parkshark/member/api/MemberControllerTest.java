package com.switchfully.parkshark.member.api;

import com.switchfully.parkshark.member.domain.LicensePlateRepository;
import com.switchfully.parkshark.member.domain.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerIntegrationTest {

    private static final String VALID_MEMBER = """
            {
              "firstName": "Peter",
              "lastName": "Parker",
              "telephone": "0475555555",
              "email": "peter.parker@mail.be",
              "licensePlate": { "plateNumber": "1-SPI-001", "country": "Belgium" },
              "address": {
                "streetName": "Naamsestraat",
                "streetNumber": "22",
                "postalCode": { "zip": "3000", "label": "Leuven" }
              }
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LicensePlateRepository licensePlateRepository;

    @Test
    void createMember_givenValidData_thenReturns201WithTheMember() throws Exception {
        long membersBefore = memberRepository.count();

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Peter"))
                .andExpect(jsonPath("$.lastName").value("Parker"))
                .andExpect(jsonPath("$.email").value("peter.parker@mail.be"))
                .andExpect(jsonPath("$.licensePlate.plateNumber").value("1-SPI-001"))
                .andExpect(jsonPath("$.licensePlate.country").value("Belgium"))
                .andExpect(jsonPath("$.address.postalCode.label").value("Leuven"))
                .andExpect(jsonPath("$.registrationDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.membershipLevel").value("BRONZE"));

        assertThat(memberRepository.count()).isEqualTo(membersBefore + 1);
        assertThat(licensePlateRepository.existsById("1-SPI-001")).isTrue();
    }

    @Test
    void createMember_givenPlateOfAnotherMember_thenReturns400() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER.replace("1-SPI-001", "1-ABC-123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("A member with that license plate number already exists"));
    }

    @Test
    void createMember_givenInvalidEmail_thenReturns400() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER.replace("peter.parker@mail.be", "not-an-email")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMember_givenNoTelephone_thenReturns400() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER.replace("\"0475555555\"", "null")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMember_givenPlateWithoutCountry_thenReturns400() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER.replace("\"Belgium\"", "\"\"")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMember_givenNoLicensePlate_thenReturns400() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER.replace(
                                "{ \"plateNumber\": \"1-SPI-001\", \"country\": \"Belgium\" }", "null")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMember_withoutMembershipLevel_thenIsBronze() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.membershipLevel").value("BRONZE"));
    }
    @Test
    void createMember_withGoldLevel_thenIsGold() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER.replace(
                                "\"email\": \"peter.parker@mail.be\",",
                                "\"email\": \"peter.parker@mail.be\", \"membershipLevel\": \"GOLD\",")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.membershipLevel").value("GOLD"));
    }

    @Test
    void createMember_withUnknownLevel_thenReturns400() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER.replace(
                                "\"email\": \"peter.parker@mail.be\",",
                                "\"email\": \"peter.parker@mail.be\", \"membershipLevel\": \"PLATINUM\",")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllMembers_thenReturns200WithEveryMember() throws Exception {
        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) memberRepository.count()))
                .andExpect(jsonPath("$[?(@.email == 'emma.willems@mail.be')].licensePlateNumber")
                        .value(hasItem("1-ABC-123")))
                .andExpect(jsonPath("$[?(@.email == 'emma.willems@mail.be')].registrationDate")
                        .value(hasItem("2026-01-15")));
    }

    @Test
    void getAllMembers_returnsOnlyTheSummaryFields() throws Exception {
        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].lastName").exists())
                .andExpect(jsonPath("$[0].licensePlateNumber").exists())
                .andExpect(jsonPath("$[0].telephone").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].registrationDate").exists())
                .andExpect(jsonPath("$[0].address").doesNotExist())
                .andExpect(jsonPath("$[0].licensePlate").doesNotExist())
                .andExpect(jsonPath("$[0].membershipLevel").doesNotExist());
    }

    @Test
    void getAllMembers_includesANewlyRegisteredMember() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MEMBER))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].licensePlateNumber").value(hasItem("1-SPI-001")));
    }
}