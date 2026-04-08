package co.uniquindio.software.devops.service;

import co.uniquindio.software.devops.model.dto.FlightDTO;
import co.uniquindio.software.devops.model.entity.FlightStatus;
import java.util.List;

public interface FlightService {
    List<FlightDTO> findAll();
    FlightDTO findById(Long id);
    FlightDTO findByFlightNumber(String flightNumber);
    List<FlightDTO> findByStatus(FlightStatus status);
    FlightDTO save(FlightDTO dto);
    FlightDTO update(Long id, FlightDTO dto);
    FlightDTO updateStatus(Long id, FlightStatus status);
    void delete(Long id);
}