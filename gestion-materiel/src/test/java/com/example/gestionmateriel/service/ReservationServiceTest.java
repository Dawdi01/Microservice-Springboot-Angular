package com.example.gestionmateriel.service;

import com.example.gestionmateriel.dto.ReservationCreateDTO;
import com.example.gestionmateriel.model.Materiel;
import com.example.gestionmateriel.model.Reservation;
import com.example.gestionmateriel.model.ReservationStatus;
import com.example.gestionmateriel.repository.ReservationRepository;
import com.example.userapi.client.UserClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MaterielService materielService;

    @Mock
    private EmailService emailService;

    // ✅ AuthUtils supprimé

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(
                reservationRepository,
                materielService,
                emailService,
                null, // AuthUtils remplacé par null
                userClient
        );
    }

    @Test
    void testCreateReservation_withReservedBy() {
        ReservationCreateDTO dto = new ReservationCreateDTO();
        dto.setMaterielId("mat1");
        dto.setStartDate("2025-06-30T10:00:00+01:00");
        dto.setEndDate("2025-07-01T10:00:00+01:00");
        dto.setReservedBy("test_user");

        Materiel materiel = new Materiel();
        materiel.setId("mat1");

        Reservation saved = new Reservation();
        saved.setId("res1");
        saved.setMateriel(materiel);
        saved.setUserId("test_user");
        saved.setReservedBy("Utilisateur test");
        saved.setStartDate(LocalDateTime.parse("2025-06-30T10:00:00"));
        saved.setEndDate(LocalDateTime.parse("2025-07-01T10:00:00"));
        saved.setStatus(ReservationStatus.PENDING);

        when(materielService.getMaterielById("mat1")).thenReturn(materiel);
        when(reservationRepository.save(any())).thenReturn(saved);

        Reservation result = reservationService.createReservation(dto);

        assertNotNull(result);
        assertEquals("test_user", result.getUserId());
        assertEquals("Utilisateur test", result.getReservedBy());
        assertEquals(ReservationStatus.PENDING, result.getStatus());
    }

    @Test
    void testConfirmReservationAsPaid() {
        Reservation res = new Reservation();
        res.setId("res1");
        res.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findById("res1")).thenReturn(Optional.of(res));

        Reservation savedRes = new Reservation();
        savedRes.setId("res1");
        savedRes.setStatus(ReservationStatus.PAID);

        when(reservationRepository.save(any())).thenReturn(savedRes);

        reservationService.confirmReservationAsPaid("res1");

        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void testCancelReservation() {
        Reservation res = new Reservation();
        res.setId("res1");
        res.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findById("res1")).thenReturn(Optional.of(res));

        Reservation cancelled = new Reservation();
        cancelled.setId("res1");
        cancelled.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.save(any())).thenReturn(cancelled);

        reservationService.cancelReservation("res1");

        verify(reservationRepository, times(1)).save(any());
    }
}
