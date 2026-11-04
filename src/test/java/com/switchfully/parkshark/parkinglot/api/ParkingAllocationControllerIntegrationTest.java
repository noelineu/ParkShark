package com.switchfully.parkshark.parkinglot.api;

import com.jayway.jsonpath.JsonPath;
import com.switchfully.parkshark.member.domain.MemberRepository;
import com.switchfully.parkshark.parkinglot.api.dto.ParkingAllocationDto;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocation;
import com.switchfully.parkshark.parkinglot.domain.ParkingAllocationRepository;
import com.switchfully.parkshark.parkinglot.domain.ParkingLotRepository;
import com.switchfully.parkshark.parkinglot.domain.Status;
import com.switchfully.parkshark.parkinglot.service.ParkingAllocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private ParkingAllocationService parkingAllocationService;

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

    private String licensePlate(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow().getLicensePlate().getPlateNumber();
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

    @Test
    void getAllocationsByMember_withoutStatus_thenReturnsActiveAndStoppedOfThatMemberOnly() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long noah = memberId("noah.lambert@mail.be");
        Long ladeuze = parkingLotId("Ladeuze Underground");
        Long emmaActive = startAllocation(emma, ladeuze, licensePlate(emma));
        Long emmaStopped = startAllocation(emma, ladeuze, licensePlate(emma));
        stop(emmaStopped, emma).andExpect(status().isOk());
        Long noahsAllocation = startAllocation(noah, ladeuze, licensePlate(noah));

        mockMvc.perform(get("/allocations/members/" + emma))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(emmaActive.intValue())))
                .andExpect(jsonPath("$[*].id", hasItem(emmaStopped.intValue())))
                .andExpect(jsonPath("$[*].id", not(hasItem(noahsAllocation.intValue()))));
    }

    @Test
    void getAllocationsByMember_withStatusActive_thenReturnsOnlyActive() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long ladeuze = parkingLotId("Ladeuze Underground");
        Long emmaActive = startAllocation(emma, ladeuze, licensePlate(emma));
        Long emmaStopped = startAllocation(emma, ladeuze, licensePlate(emma));
        stop(emmaStopped, emma).andExpect(status().isOk());

        mockMvc.perform(get("/allocations/members/" + emma).param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status", everyItem(equalTo("ACTIVE"))))
                .andExpect(jsonPath("$[*].id", hasItem(emmaActive.intValue())))
                .andExpect(jsonPath("$[*].id", not(hasItem(emmaStopped.intValue()))));
    }

    @Test
    void getAllocationsByMember_withStatusInactive_thenReturnsOnlyStopped() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long ladeuze = parkingLotId("Ladeuze Underground");
        Long emmaActive = startAllocation(emma, ladeuze, licensePlate(emma));
        Long emmaStopped = startAllocation(emma, ladeuze, licensePlate(emma));
        stop(emmaStopped, emma).andExpect(status().isOk());

        mockMvc.perform(get("/allocations/members/" + emma).param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status", everyItem(equalTo("INACTIVE"))))
                .andExpect(jsonPath("$[*].id", hasItem(emmaStopped.intValue())))
                .andExpect(jsonPath("$[*].id", not(hasItem(emmaActive.intValue()))));
    }

    @Test
    void getAllocationsByMember_unknownMember_thenReturns404() throws Exception {
        mockMvc.perform(get("/allocations/members/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllocationsByMember_withUnknownStatus_thenReturns400() throws Exception {
        Long emma = memberId("emma.willems@mail.be");

        mockMvc.perform(get("/allocations/members/" + emma).param("status", "STOPPED"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllParkingAllocationsByParkingLot() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long parkingLotId = parkingLotId("Ladeuze Underground");

        Long allocation1 = startAllocation(
                emma, parkingLotId, licensePlate(emma));

        Long allocation2 = startAllocation(
                emma, parkingLotId, licensePlate(emma));

        mockMvc.perform(get("/allocations/parking-lots/{parkingLotId}", parkingLotId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id", hasItem(allocation1.intValue())))
                .andExpect(jsonPath("$[*].id", hasItem(allocation2.intValue())));
    }

    @Test
    void shouldGetParkingAllocationsFilteredByStatus() throws Exception {
        Long emma = memberId("emma.willems@mail.be");
        Long parkingLotId = parkingLotId("Ladeuze Underground");

        String plate = licensePlate(emma);

        Long activeAllocation = startAllocation(emma, parkingLotId, plate);
        Long inactiveAllocation = startAllocation(emma, parkingLotId, plate);

        stop(inactiveAllocation, emma)
                .andExpect(status().isOk());

        mockMvc.perform(get("/allocations/parking-lots/{parkingLotId}", parkingLotId)
                        .param("status", Status.ACTIVE.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].status", everyItem(equalTo("ACTIVE"))))
                .andExpect(jsonPath("$[*].id", hasItem(activeAllocation.intValue())))
                .andExpect(jsonPath("$[*].id", not(hasItem(inactiveAllocation.intValue()))));
    }
}