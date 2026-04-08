package co.uniquindio.software.devops.service;

import co.uniquindio.software.devops.model.dto.PassengerDTO;
import java.util.List;

public interface PassengerService {
    List<PassengerDTO> findAll();
    PassengerDTO findById(Long id);
    List<PassengerDTO> findByFlightId(Long flightId);
    PassengerDTO findByEmail(String email);
    PassengerDTO save(PassengerDTO dto);
    PassengerDTO update(Long id, PassengerDTO dto);
    void delete(Long id);
}