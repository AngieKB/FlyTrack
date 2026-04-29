package co.uniquindio.software.devops.service;

import co.uniquindio.software.devops.model.dto.GateDTO;
import java.util.List;

public interface GateService {
    List<GateDTO> findAll();
    GateDTO findById(Long id);
    List<GateDTO> findAvailable();
    GateDTO save(GateDTO dto);
    GateDTO update(Long id, GateDTO dto);
    void delete(Long id);
}