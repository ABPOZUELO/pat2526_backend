package edu.comillas.icai.gitt.pat.spring.mvc;

import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ControladorRest.class)
class ControladorRestIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void creaContadorOkTest() throws Exception {
        // Given ...
        String contador = "{\"nombre\":\"visitas\",\"valor\":0}";
        // When ...
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/contadores")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(contador))
                // Then ...
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(contador));
    }
    @Test
    void creaContadorIncorrectoTest() throws Exception {
        // Given
        String contador = "{\"nombre\":\"\",\"valor\":0}"; // Contador con nombre vacío
        //String contador = "{\"nombre\":\"visitas\",\"valor\":0}";
        // When
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/contadores")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(contador))

                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Esperando un estado de error 400
    }
}

