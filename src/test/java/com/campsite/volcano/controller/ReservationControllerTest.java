package com.campsite.volcano.controller;


import com.campsite.volcano.repository.ReservationRepository;
import com.campsite.volcano.util.ReservationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Execution(CONCURRENT)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationController reservationController;
    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationControllerTest(ReservationRepository reservationRepository, ReservationController reservationController) {
        this.reservationController = reservationController;
        this.reservationRepository = reservationRepository;

    }

    @Test
    void availailitiesTestWithLoad() throws Exception {
        int repeat = 100;
        while(repeat-- > 0) {
            mockMvc.perform(get("/availabilities")
                            .contentType("application/json"));
                    //.andExpect(status().isOk());
        }
    }

    @Test
    void makeReservation() throws Exception {
        boolean available = reservationRepository.checkDateAvailable(10232022);
        mockMvc.perform(post("/make-reservation")
                            .contentType("application/json")
                            .param("userEmail", "amie@gmail.com")
                            .param("startDate", "10232022")
                            .param("endDate", "10252022"))
                    .andExpect(available? status().isOk(): status().isBadRequest());
        assertThat(reservationRepository.checkDateAvailable(10232022)).isFalse();

    }

    @Test
    void makeDupReservation() throws Exception {
        Thread.sleep(500);
        boolean available = reservationRepository.checkDateAvailable(10232022);
        mockMvc.perform(post("/make-reservation")
                        .contentType("application/json")
                        .param("userEmail", "amie@gmail.com")
                        .param("startDate", "10232022")
                        .param("endDate", "10252022"))
                .andExpect(available? status().isOk(): status().isBadRequest());
        assertThat(reservationRepository.checkDateAvailable(10232022)).isFalse();
    }

    @Test
    void makeDupReservationWithWrongDateRange() throws Exception {
        // works only when the entire test ran together
        mockMvc.perform(post("/make-reservation")
                        .contentType("application/json")
                        .param("userEmail", "amie@gmail.com")
                        .param("startDate", "10232022")
                        .param("endDate", "10212022"))
                .andExpect(status().isBadRequest());
        //assertThat(reservationRepository.checkDateAvailable(10232022)).isFalse();
    }

    @Test
    void makeSecondReservation() throws Exception {
        // works only when the entire test ran together
        Thread.sleep(100);
        boolean available = reservationRepository.checkDateAvailable(10272022);
        mockMvc.perform(post("/make-reservation")
                        .contentType("application/json")
                        .param("userEmail", "peter@gmail.com")
                        .param("startDate", "10272022")
                        .param("endDate", "10292022"))
                .andExpect(available? status().isOk(): status().isBadRequest());
        assertThat(reservationRepository.checkDateAvailable(10272022)).isFalse();
    }

    @Test
    void cancelReservation() throws Exception {
        // works only when the entire test ran together
        //boolean available = reservationRepository.checkDateAvailable(10232022);
        Thread.sleep(3000);
        mockMvc.perform(post("/cancel-reservation")
                        .contentType("application/json")
                        .param("userEmail", reservationRepository
                                .findByReservationId(1).get().getUser().getEmail())
                        .param("reservationId", "1"))
                .andExpect(status().isOk());
        assertThat(reservationRepository.checkDateAvailable(10232022)).isTrue();
    }


}
