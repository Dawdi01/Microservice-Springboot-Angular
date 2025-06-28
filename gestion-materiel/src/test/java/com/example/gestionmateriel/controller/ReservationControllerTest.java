package com.example.gestionmateriel.controller;

import com.example.gestionmateriel.dto.ReservationCreateDTO;
import com.example.gestionmateriel.dto.ReservationResponseDTO;
import com.example.gestionmateriel.model.Reservation;
import com.example.gestionmateriel.model.ReservationStatus;
import com.example.gestionmateriel.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.userapi.client.UserClient;
import com.example.gestionmateriel.service.MaterielService;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private UserClient userClient;

    @MockBean
    private MaterielService materielService;



    @Test
    void testCreateReservation() throws Exception {
        ReservationCreateDTO dto = new ReservationCreateDTO();
        Reservation reservation = new Reservation();

        when(reservationService.createReservation(any())).thenReturn(reservation);
        when(reservationService.mapToDTO(any())).thenReturn(new ReservationResponseDTO());

        String json = new ObjectMapper().writeValueAsString(dto);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testCancelReservation() throws Exception {
        doNothing().when(reservationService).cancelReservation("res123");

        mockMvc.perform(post("/api/reservations/res123/cancel"))
                .andExpect(status().isOk());
    }

    @Test
    void testConfirmReservation() throws Exception {
        Reservation reservation = new Reservation();
        ReservationResponseDTO dto = new ReservationResponseDTO();

        doNothing().when(reservationService).confirmReservationAsPaid("res123");
        when(reservationService.getById("res123")).thenReturn(reservation);
        when(reservationService.mapToDTO(reservation)).thenReturn(dto);

        mockMvc.perform(post("/api/reservations/res123/confirm"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetReservationsByStatus() throws Exception {
        when(reservationService.getReservationsByStatus(ReservationStatus.CONFIRMED))
                .thenReturn(List.of(new Reservation()));
        when(reservationService.mapToDTO(any())).thenReturn(new ReservationResponseDTO());

        mockMvc.perform(get("/api/reservations/status/CONFIRMED"))
                .andExpect(status().isOk());
    }
}