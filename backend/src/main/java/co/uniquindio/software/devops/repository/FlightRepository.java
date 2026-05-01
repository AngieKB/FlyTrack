package co.uniquindio.software.devops.repository;

import co.uniquindio.software.devops.model.entity.Flight;
import co.uniquindio.software.devops.model.entity.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumber(String flightNumber);
    List<Flight> findByStatus(FlightStatus status);
    List<Flight> findByOriginAndDestination(String origin, String destination);
}