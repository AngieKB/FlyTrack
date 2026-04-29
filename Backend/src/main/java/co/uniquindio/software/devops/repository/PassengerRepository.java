package co.uniquindio.software.devops.repository;

import co.uniquindio.software.devops.model.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByEmail(String email);
    Optional<Passenger> findByDocumentNumber(String documentNumber);
    List<Passenger> findByFlightId(Long flightId);
}