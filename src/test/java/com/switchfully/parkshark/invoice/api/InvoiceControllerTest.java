package com.switchfully.parkshark.invoice.api;

import com.jayway.jsonpath.JsonPath;
import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocationRepository;
import com.switchfully.parkshark.parkinglot.domain.ParkingLotRepository;
import com.switchfully.parkshark.parkinglot.domain.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingAllocationRepository allocationRepository;

    private Long memberId(String email) {
        return memberRepository.findAll().stream()
                .filter(member -> member.getEmail().equals(email))
                .findFirst().orElseThrow().getId();
    }

    private Long parkingLotId(String name) {
        return parkingLotRepository.findAll().stream()
                .filter(parkingLot -> parkingLot.getName().equals(name))
                .findFirst().orElseThrow().getId();
    }

    private Long startAndStopAllocation(Long memberId, Long parkingLotId, String plate) throws Exception {
        MvcResult result = mockMvc.perform(post("/allocations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "memberId": %d, "parkingLotId": %d, "licensePlateNumber": "%s" }
                                """.formatted(memberId, parkingLotId, plate)))
                .andExpect(status().isCreated())
                .andReturn();
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        Long allocationId = id.longValue();

        mockMvc.perform(post("/allocations/" + allocationId + "/stop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"memberId\": " + memberId + " }"))
                .andExpect(status().isOk());
        return allocationId;
    }

    @Test
    void createInvoice_containsOnlyTheStoppedAllocations() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long stoppedId = startAndStopAllocation(emma, parkingLotId("Ladeuze Underground"), "1-ABC-123");

        mockMvc.perform(post("/members/" + emma + "/invoices"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value(startsWith("INV-")))
                .andExpect(jsonPath("$.memberId").value(emma.intValue()))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.creationDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.expirationDate").value(LocalDate.now().plusDays(30).toString()))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].parkingAllocationId").value(stoppedId.intValue()));
    }

    @Test
    void createInvoice_marksTheAllocationsAsInvoiced() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long stoppedId = startAndStopAllocation(emma, parkingLotId("Ladeuze Underground"), "1-ABC-123");

        mockMvc.perform(post("/members/" + emma + "/invoices")).andExpect(status().isCreated());

        assertThat(allocationRepository.findById(stoppedId).orElseThrow().getStatus())
                .isEqualTo(Status.INVOICED);
    }

    @Test
    void secondInvoice_doesNotInvoiceTheSameAllocationsAgain() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        startAndStopAllocation(emma, parkingLotId("Ladeuze Underground"), "1-ABC-123");
        mockMvc.perform(post("/members/" + emma + "/invoices")).andExpect(status().isCreated());

        mockMvc.perform(post("/members/" + emma + "/invoices"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void goldMemberWithoutStoppedAllocation_paysTheMonthlyCost() throws Exception {
        mockMvc.perform(post("/members/" + memberId("olivia.maes@mail.be") + "/invoices"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.membershipCost").value(40))
                .andExpect(jsonPath("$.totalPrice").value(40));
    }

    @Test
    void createInvoice_forUnknownMember_thenReturns404() throws Exception {
        mockMvc.perform(post("/members/999999/invoices"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No member found for id: 999999"));
    }
}