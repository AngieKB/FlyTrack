package co.uniquindio.software.devops.service.implementation;

import co.uniquindio.software.devops.model.dto.BaggageReportDTO;
import co.uniquindio.software.devops.model.entity.*;
import co.uniquindio.software.devops.exception.ResourceNotFoundException;
import co.uniquindio.software.devops.repository.BaggageReportRepository;
import co.uniquindio.software.devops.repository.PassengerRepository;
import co.uniquindio.software.devops.service.BaggageReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaggageReportServiceImpl implements BaggageReportService {

    private final BaggageReportRepository baggageReportRepository;
    private final PassengerRepository passengerRepository;

    @Override
    public List<BaggageReportDTO> findAll() {
        return baggageReportRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public BaggageReportDTO findById(Long id) {
        return toDTO(baggageReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id)));
    }

    @Override
    public List<BaggageReportDTO> findByPassengerId(Long passengerId) {
        return baggageReportRepository.findByPassengerId(passengerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public BaggageReportDTO save(BaggageReportDTO dto) {
        BaggageReport report = toEntity(dto);
        report.setStatus(BaggageStatus.REPORTED);
        report.setReportedAt(LocalDateTime.now());
        return toDTO(baggageReportRepository.save(report));
    }

    @Override
    public BaggageReportDTO updateStatus(Long id, BaggageStatus status) {
        BaggageReport report = baggageReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));
        report.setStatus(status);
        return toDTO(baggageReportRepository.save(report));
    }

    @Override
    public void delete(Long id) {
        if (!baggageReportRepository.existsById(id))
            throw new ResourceNotFoundException("Reporte no encontrado con id: " + id);
        baggageReportRepository.deleteById(id);
    }

    private BaggageReportDTO toDTO(BaggageReport r) {
        return BaggageReportDTO.builder().id(r.getId()).description(r.getDescription())
                .status(r.getStatus()).reportedAt(r.getReportedAt()).baggageTag(r.getBaggageTag())
                .passengerId(r.getPassenger() != null ? r.getPassenger().getId() : null).build();
    }

    private BaggageReport toEntity(BaggageReportDTO dto) {
        BaggageReport.BaggageReportBuilder builder = BaggageReport.builder()
                .description(dto.getDescription()).baggageTag(dto.getBaggageTag());
        if (dto.getPassengerId() != null) {
            Passenger passenger = passengerRepository.findById(dto.getPassengerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado"));
            builder.passenger(passenger);
        }
        return builder.build();
    }
}