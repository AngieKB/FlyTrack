package co.uniquindio.software.devops.service.implementation;

import co.uniquindio.software.devops.model.dto.FlightDTO;
import co.uniquindio.software.devops.model.entity.*;
import co.uniquindio.software.devops.exception.ResourceNotFoundException;
import co.uniquindio.software.devops.repository.FlightRepository;
import co.uniquindio.software.devops.repository.GateRepository;
import co.uniquindio.software.devops.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final GateRepository gateRepository;

    @Override
    public List<FlightDTO> findAll() {
        return flightRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public FlightDTO findById(Long id) {
        return toDTO(flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id)));
    }

    @Override
    public FlightDTO findByFlightNumber(String flightNumber) {
        return toDTO(flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado: " + flightNumber)));
    }

    @Override
    public List<FlightDTO> findByStatus(FlightStatus status) {
        return flightRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public FlightDTO save(FlightDTO dto) {
        Flight flight = toEntity(dto);
        flight.setStatus(FlightStatus.SCHEDULED);
        return toDTO(flightRepository.save(flight));
    }

    @Override
    public FlightDTO update(Long id, FlightDTO dto) {
        Flight existing = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));
        existing.setFlightNumber(dto.getFlightNumber());
        existing.setOrigin(dto.getOrigin());
        existing.setDestination(dto.getDestination());
        existing.setDepartureTime(dto.getDepartureTime());
        existing.setArrivalTime(dto.getArrivalTime());
        existing.setAirline(dto.getAirline());
        if (dto.getGateId() != null) {
            Gate gate = gateRepository.findById(dto.getGateId())
                    .orElseThrow(() -> new ResourceNotFoundException("Puerta no encontrada con id: " + dto.getGateId()));
            existing.setGate(gate);
        }
        return toDTO(flightRepository.save(existing));
    }

    @Override
    public FlightDTO updateStatus(Long id, FlightStatus status) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));
        flight.setStatus(status);
        return toDTO(flightRepository.save(flight));
    }

    @Override
    public void delete(Long id) {
        if (!flightRepository.existsById(id))
            throw new ResourceNotFoundException("Vuelo no encontrado con id: " + id);
        flightRepository.deleteById(id);
    }

    private FlightDTO toDTO(Flight f) {
        return FlightDTO.builder()
                .id(f.getId()).flightNumber(f.getFlightNumber())
                .origin(f.getOrigin()).destination(f.getDestination())
                .departureTime(f.getDepartureTime()).arrivalTime(f.getArrivalTime())
                .status(f.getStatus()).airline(f.getAirline())
                .gateId(f.getGate() != null ? f.getGate().getId() : null)
                .gateCode(f.getGate() != null ? f.getGate().getGateCode() : null)
                .build();
    }

    private Flight toEntity(FlightDTO dto) {
        Flight.FlightBuilder builder = Flight.builder()
                .flightNumber(dto.getFlightNumber()).origin(dto.getOrigin())
                .destination(dto.getDestination()).departureTime(dto.getDepartureTime())
                .arrivalTime(dto.getArrivalTime()).airline(dto.getAirline());
        if (dto.getGateId() != null) {
            Gate gate = gateRepository.findById(dto.getGateId())
                    .orElseThrow(() -> new ResourceNotFoundException("Puerta no encontrada"));
            builder.gate(gate);
        }
        return builder.build();
    }
}