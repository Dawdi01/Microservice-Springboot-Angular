package com.example.gestionmateriel.controller;

import com.example.gestionmateriel.model.Materiel;
import com.example.gestionmateriel.service.MaterielService;
import com.example.userapi.client.UserClient;
import com.example.userapi.dto.UserResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MaterielControllerTest {

    private MaterielService materielService;
    private UserClient userClient;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        materielService = Mockito.mock(MaterielService.class);
        userClient = Mockito.mock(UserClient.class);
        MaterielController controller = new MaterielController(materielService, userClient);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllMateriels() throws Exception {
        mockMvc.perform(get("/api/materiels"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateMateriel() throws Exception {
        Materiel materiel = new Materiel();
        materiel.setName("Test");
        materiel.setSportType("Football");
        materiel.setPrice(150.0);
        materiel.setState("neuf");

        // simulate full valid user
        UserResponseDTO user = Mockito.mock(UserResponseDTO.class);
        when(userClient.getCurrentUser(any())).thenReturn(user);
        when(materielService.createMateriel(any())).thenReturn(materiel);

        mockMvc.perform(post("/api/materiels")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(materiel)))
                .andExpect(status().isCreated());
    }

    @Test
    void testDeleteMateriel() throws Exception {
        doNothing().when(materielService).deleteMateriel("123");

        mockMvc.perform(delete("/api/materiels/123"))
                .andExpect(status().isNoContent());
    }
}
