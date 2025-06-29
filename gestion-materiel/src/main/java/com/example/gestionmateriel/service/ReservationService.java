package com.example.gestionmateriel.service;

import com.example.gestionmateriel.config.AuthUtils;
import com.example.gestionmateriel.dto.ReservationCreateDTO;
import com.example.gestionmateriel.dto.ReservationResponseDTO;
import com.example.gestionmateriel.model.Materiel;
import com.example.gestionmateriel.model.Reservation;
import com.example.gestionmateriel.model.ReservationStatus;
import com.example.gestionmateriel.repository.ReservationRepository;
import com.example.userapi.client.UserClient;
import com.example.userapi.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MaterielService materielService;
    private final EmailService emailService;
    private final AuthUtils authUtils;
    private final UserClient userClient;


    public Reservation createReservation(ReservationCreateDTO dto) {
        Materiel materiel = materielService.getMaterielById(dto.getMaterielId());

        Reservation reservation = new Reservation();
        reservation.setMateriel(materiel);
        reservation.setStartDate(OffsetDateTime.parse(dto.getStartDate()).toLocalDateTime());
        reservation.setEndDate(OffsetDateTime.parse(dto.getEndDate()).toLocalDateTime());
        reservation.setStatus(ReservationStatus.PENDING);

        if (dto.getReservedBy() != null && !dto.getReservedBy().isEmpty()) {
            reservation.setUserId(dto.getReservedBy());
            reservation.setReservedBy("Utilisateur test");
        } else {
            String userId = authUtils.getCurrentUserId();
            reservation.setUserId(userId);
            UserResponseDTO user = userClient.getUserById(userId, null);
            reservation.setReservedBy(user.nomuser() + " " + user.prenomuser());
        }

        Reservation saved = reservationRepository.save(reservation);

        // ✅ Envoi de l’email après la création
        sendReservationEmail(saved);

        return saved;
    }


    public ReservationResponseDTO mapToDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUserId());
        dto.setStartDate(reservation.getStartDate());
        dto.setEndDate(reservation.getEndDate());
        dto.setStatus(reservation.getStatus().toString());
        dto.setReservedBy(reservation.getReservedBy());
        dto.setPaid(reservation.getStatus() == ReservationStatus.PAID);

        if (reservation.getMateriel() != null) {
            dto.setMaterielId(reservation.getMateriel().getId());
            dto.setMaterielName(reservation.getMateriel().getName());
            dto.setMaterielImageUrl(reservation.getMateriel().getImageUrl());
        }

        return dto;
    }

    public List<Reservation> getReservationsByReservedBy(String reservedBy) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getReservedBy() != null && r.getReservedBy().equalsIgnoreCase(reservedBy))
                .collect(Collectors.toList());
    }

    public void confirmReservationAsPaid(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);
        sendReservationEmail(reservation);
    }

    public void cancelReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public List<Reservation> findByMaterielId(String id) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getMateriel() != null && id.equals(r.getMateriel().getId()))
                .collect(Collectors.toList());
    }

    public Reservation getById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
    }

    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void sendReservationEmail(Reservation reservation) {
        try {
            UserResponseDTO user = userClient.getUserById(reservation.getUserId(), null);
            Materiel materiel = reservation.getMateriel();

            String subject = reservation.getStatus() == ReservationStatus.PAID
                    ? "✅ Paiement confirmé - Réservation"
                    : "📅 Réservation en attente de paiement";

            String message = "Bonjour " + user.nomuser() + ",\n\n"
                    + "Voici un récapitulatif de votre réservation :\n\n"
                    + "📦 Matériel : " + (materiel != null ? materiel.getName() : "non spécifié") + "\n"
                    + "📅 Du : " + reservation.getStartDate() + "\n"
                    + "📅 Au : " + reservation.getEndDate() + "\n"
                    + "💬 Statut : " + reservation.getStatus().name() + "\n\n"
                    + "Merci pour votre confiance !\n";

            emailService.sendReservationConfirmation(user.mailuser(), subject, message);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
    public Reservation getReservationById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec l'ID : " + id));
    }
}
