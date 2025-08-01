package com.example.gestionmateriel.repository;

import com.example.gestionmateriel.model.Reservation;
import com.example.gestionmateriel.model.ReservationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {

    // Liste des réservations par matériel
    List<Reservation> findByMaterielId(String id);

    // Liste des réservations selon le statut
    List<Reservation> findByStatus(ReservationStatus status);

    // Vérification des conflits de réservation (intersection de dates)
    List<Reservation> findByMaterielIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String id, LocalDateTime end, LocalDateTime start);

    // Compter le nombre de réservations d’un matériel
    int countByMaterielId(String id);

    // Filtrer par nom ou ID du réservant
    List<Reservation> findByReservedBy(String reservedBy);

    // Avec filtre combiné
    List<Reservation> findByReservedByAndStatus(String reservedBy, ReservationStatus status);
}
