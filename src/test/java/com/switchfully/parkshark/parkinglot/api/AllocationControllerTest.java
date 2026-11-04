package com.switchfully.parkshark.parkinglot.api;

import com.switchfully.parkshark.parkinglot.domain.ParkingLotAllocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AllocationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParkingLotAllocationRepository parkingLotAllocationRepository;

    @Test
    void getAllAllocations_withoutFilters_thenReturnsAll() throws Exception {
        long expected = parkingLotAllocationRepository.count();

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