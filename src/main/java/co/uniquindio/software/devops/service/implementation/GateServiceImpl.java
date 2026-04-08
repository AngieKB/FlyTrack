package co.uniquindio.software.devops.service.implementation;

import co.uniquindio.software.devops.model.dto.GateDTO;
import co.uniquindio.software.devops.model.entity.Gate;
import co.uniquindio.software.devops.exception.ResourceNotFoundException;
import co.uniquindio.software.devops.repository.GateRepository;
import co.uniquindio.software.devops.service.GateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GateServiceImpl implements GateService {

    private final GateRepository gateRepository;

    @Override
    public List<GateDTO> findAll() {
        return gateRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public GateDTO findById(Long id) {
        return toDTO(gateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puerta no encontrada con id: " + id)));
    }

    @Override
    public List<GateDTO> findAvailable() {
        return gateRepository.findByAvailable(true).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public GateDTO save(GateDTO dto) {
        return toDTO(gateRepository.save(toEntity(dto)));
    }

    @Override
    public GateDTO update(Long id, GateDTO dto) {
        Gate existing = gateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puerta no encontrada con id: " + id));
        existing.setGateCode(dto.getGateCode());
        existing.setTerminal(dto.getTerminal());
        existing.setAvailable(dto.isAvailable());
        return toDTO(gateRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!gateRepository.existsById(id))
            throw new ResourceNotFoundException("Puerta no encontrada con id: " + id);
        gateRepository.deleteById(id);
    }

    private GateDTO toDTO(Gate g) {
        return GateDTO.builder().id(g.getId()).gateCode(g.getGateCode())
                .terminal(g.getTerminal()).available(g.isAvailable()).build();
    }

    private Gate toEntity(GateDTO dto) {
        return Gate.builder().gateCode(dto.getGateCode())
                .terminal(dto.getTerminal()).available(dto.isAvailable()).build();
    }
}