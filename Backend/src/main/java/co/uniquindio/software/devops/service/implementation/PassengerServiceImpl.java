package co.uniquindio.software.devops.service.implementation;

import co.uniquindio.software.devops.model.dto.PassengerDTO;
import co.uniquindio.software.devops.model.entity.*;
import co.uniquindio.software.devops.exception.ResourceNotFoundException;
import co.uniquindio.software.devops.repository.FlightRepository;
import co.uniquindio.software.devops.repository.PassengerRepository;
import co.uniquindio.software.devops.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;

    @Override
    public List<PassengerDTO> findAll() {
        return passengerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PassengerDTO findById(Long id) {
        return toDTO(passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado con id: " + id)));
    }

    @Override
    public List<PassengerDTO> findByFlightId(Long flightId) {
        return passengerRepository.findByFlightId(flightId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PassengerDTO findByEmail(String email) {
        return toDTO(passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado: " + email)));
    }

    @Override
    public PassengerDTO save(PassengerDTO dto) {
        return toDTO(passengerRepository.save(toEntity(dto)));
    }

    @Override
    public PassengerDTO update(Long id, PassengerDTO dto) {
        Passenger existing = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado con id: " + id));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setDocumentNumber(dto.getDocumentNumber());
        existing.setSeatNumber(dto.getSeatNumber());
        if (dto.getFlightId() != null) {
            Flight flight = flightRepository.findById(dto.getFlightId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado"));
            existing.setFlight(flight);
        }
        return toDTO(passengerRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!passengerRepository.existsById(id))
            throw new ResourceNotFoundException("Pasajero no encontrado con id: " + id);
        passengerRepository.deleteById(id);
    }

    private PassengerDTO toDTO(Passenger p) {
        return PassengerDTO.builder().id(p.getId()).firstName(p.getFirstName())
                .lastName(p.getLastName()).email(p.getEmail())
                .documentNumber(p.getDocumentNumber()).seatNumber(p.getSeatNumber())
                .flightId(p.getFlight() != null ? p.getFlight().getId() : null).build();
    }

    private Passenger toEntity(PassengerDTO dto) {
        Passenger.PassengerBuilder builder = Passenger.builder()
                .firstName(dto.getFirstName()).lastName(dto.getLastName())
                .email(dto.getEmail()).documentNumber(dto.getDocumentNumber())
                .seatNumber(dto.getSeatNumber());
        if (dto.getFlightId() != null) {
            Flight flight = flightRepository.findById(dto.getFlightId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado"));
            builder.flight(flight);
        }
        return builder.build();
    }
}