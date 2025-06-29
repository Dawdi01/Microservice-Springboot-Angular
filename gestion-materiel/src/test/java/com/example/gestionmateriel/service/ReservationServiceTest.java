package com.example.gestionmateriel.service;

import com.example.gestionmateriel.config.AuthUtils;
import com.example.gestionmateriel.dto.ReservationCreateDTO;
import com.example.gestionmateriel.model.Materiel;
import com.example.gestionmateriel.model.Reservation;
import com.example.gestionmateriel.model.ReservationStatus;
import com.example.gestionmateriel.repository.ReservationRepository;
import com.example.userapi.client.UserClient;
import com.example.userapi.dto.UserResponseDTO;
import com.example.userapi.model.Job;
import com.example.userapi.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private MaterielService materielService;
    @Mock private EmailService emailService;
    @Mock private UserClient userClient;
    @Mock private AuthUtils authUtils;

    @InjectMocks private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

  /*  public Reservation createReservation(ReservationCreateDTO dto) {
        // 1. Récupération du token
        String token = authUtils.getCurrentToken();

        // 2. Récupération de l'utilisateur courant via le token
        UserResponseDTO user = userClient.getCurrentUser(token);

        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        // 3. Récupération du matériel
        Materiel materiel = materielService.getMaterielById(dto.getMaterielId());

        // 4. Construction de la réservation
        Reservation reservation = new Reservation();
        reservation.setMateriel(materiel);
        reservation.setUserId(user.getId());
        reservation.setReservedBy(user.getPrenomuser() + " " + user.getNomuser());
        reservation.setStartDate(LocalDateTime.parse(dto.getStartDate().substring(0, 19)));
        reservation.setEndDate(LocalDateTime.parse(dto.getEndDate().substring(0, 19)));
        reservation.setStatus(ReservationStatus.PENDING);

        // 5. Sauvegarde
        Reservation savedReservation = reservationRepository.save(reservation);

        // 6. Notification
        try {
            emailService.sendReservationConfirmation(
                    user.getEmail(),
                    reservation.getReservedBy(),
                    materiel.getName()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }

        return savedReservation;
    }*/



    @Test
    void testConfirmReservationAsPaid() {
        Reservation res = new Reservation();
        res.setId("res1");
        res.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findById("res1")).thenReturn(Optional.of(res));
        when(reservationRepository.save(any())).thenReturn(res);

        reservationService.confirmReservationAsPaid("res1");

        verify(reservationRepository).save(any());
        assertEquals(ReservationStatus.PAID, res.getStatus());
    }

    @Test
    void testCancelReservation() {
        Reservation res = new Reservation();
        res.setId("res1");
        res.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findById("res1")).thenReturn(Optional.of(res));
        when(reservationRepository.save(any())).thenReturn(res);

        reservationService.cancelReservation("res1");

        verify(reservationRepository).save(any());
        assertEquals(ReservationStatus.CANCELLED, res.getStatus());
    }

    @Test
    void testGetAllReservations() {
        List<Reservation> reservations = List.of(new Reservation(), new Reservation());
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> result = reservationService.getAllReservations();

        assertEquals(2, result.size());
        verify(reservationRepository).findAll();
    }

    @Test
    void testGetReservationByIdFound() {
        Reservation res = new Reservation();
        res.setId("res123");

        when(reservationRepository.findById("res123")).thenReturn(Optional.of(res));

        Reservation found = reservationService.getReservationById("res123");

        assertNotNull(found);
        assertEquals("res123", found.getId());
    }

    @Test
    void testGetReservationByIdNotFound() {
        when(reservationRepository.findById("notFound")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.getReservationById("notFound"));

        assertEquals("Réservation introuvable avec l'ID : notFound", exception.getMessage());
    }
}
