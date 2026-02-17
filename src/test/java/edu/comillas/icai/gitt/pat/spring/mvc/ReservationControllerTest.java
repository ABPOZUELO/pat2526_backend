package edu.comillas.icai.gitt.pat.spring.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false) // Esta es la magia para saltarnos el error 401
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getReservations_DebeDevolver200() throws Exception {
        mockMvc.perform(get("/pistaPadel/reservations"))
                .andExpect(status().isOk());
    }
}