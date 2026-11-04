package com.switchfully.parkshark.parkinglot.api;

import com.jayway.jsonpath.JsonPath;
import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocation;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocationRepository;
import com.switchfully.parkshark.parkinglot.domain.ParkingLotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ParkingAllocationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParkingAllocationRepository parkingAllocationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

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

    private String allocationRequest(Long memberId, Long parkingLotId, String plate) {
        return """
                { "memberId": %d, "parkingLotId": %d, "licensePlateNumber": "%s" }
                """.formatted(memberId, parkingLotId, plate);
    }

    private Long startAllocation(Long memberId, Long parkingLotId, String plate) throws Exception {
        MvcResult result = mockMvc.perform(post("/allocations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(allocationRequest(memberId, parkingLotId, plate)))
                .andExpect(status().isCreated())
                .andReturn();
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    private ResultActions stop(Long allocationId, Long memberId) throws Exception {
        return mockMvc.perform(post("/allocations/" + allocationId + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"memberId\": " + memberId + " }"));
    }

    @Test
    void stop_byTheOwner_thenReturns200WithStoppedAllocation() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long allocationId = startAllocation(emma, parkingLotId("Ladeuze Underground"), "1-ABC-123");

        stop(allocationId, emma)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(allocationId.intValue()))
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andExpect(jsonPath("$.stopTime").isNotEmpty());
    }

    @Test
    void stop_twice_thenReturns400() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long allocationId = startAllocation(emma, parkingLotId("Ladeuze Underground"), "1-ABC-123");
        stop(allocationId, emma).andExpect(status().isOk());

        stop(allocationId, emma)
                .andExpect(status().isBadRequest());
    }

    @Test
    void stop_byAnotherMember_thenReturns403() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long noah = memberId("noah.lambert@mail.be");
        Long allocationId = startAllocation(emma, parkingLotId("Ladeuze Underground"), "1-ABC-123");

        stop(allocationId, noah)
                .andExpect(status().isForbidden());
    }

    @Test
    void stop_unknownAllocation_thenReturns404() throws Exception {
        stop(999999L, memberId("emma.willems@mail.be"))
                .andExpect(status().isNotFound());
    }

    @Test
    void stop_withoutMemberId_thenReturns400() throws Exception {
        mockMvc.perform(post("/allocations/1/stop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void stop_freesASpaceInAFullParkingLot() throws Exception {
        Long tinySharkBox = parkingLotId("Tiny Shark Box");   // 2 places, already 2 active allocations (V2)
        Long emma = memberId("emma.willems@mail.be");
        String emmaRequest = allocationRequest(emma, tinySharkBox, "1-ABC-123");

        mockMvc.perform(post("/allocations").contentType(MediaType.APPLICATION_JSON).content(emmaRequest))
                .andExpect(status().isBadRequest());   // full

        ParkingAllocation noahsAllocation = parkingAllocationRepository.findAll().stream()
                .filter(allocation -> allocation.getParkingLot().getName().equals("Tiny Shark Box")
                        && allocation.getMember().getEmail().equals("noah.lambert@mail.be"))
                .findFirst().orElseThrow();
        stop(noahsAllocation.getId(), noahsAllocation.getMember().getId())
                .andExpect(status().isOk());

        mockMvc.perform(post("/allocations").contentType(MediaType.APPLICATION_JSON).content(emmaRequest))
                .andExpect(status().isCreated());   // one space freed
    }

    @Test
    void getAllAllocations_withoutFilters_thenReturnsAll() throws Exception {
        long expected = parkingAllocationRepository.count();

        mockMvc.perform(get("/allocations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) expected));
    }

    @Test
    void getAllAllocations_withLimit_thenReturnsThatAmount() throws Exception {
        mockMvc.perform(get("/allocations").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllAllocations_withStatusActive_thenReturnsOnlyActive() throws Exception {
        mockMvc.perform(get("/allocations").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status", everyItem(equalTo("ACTIVE"))));
    }

    @Test
    void getAllAllocations_withLimitZero_thenReturns400() throws Exception {
        mockMvc.perform(get("/allocations").param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllAllocations_withUnknownStatus_thenReturns400() throws Exception {
        mockMvc.perform(get("/allocations").param("status", "STOPPED"))
                .andExpect(status().isBadRequest());
    }
}