package co.uniquindio.software.devops.service;

import co.uniquindio.software.devops.model.dto.BaggageReportDTO;
import co.uniquindio.software.devops.model.entity.BaggageStatus;
import java.util.List;

public interface BaggageReportService {
    List<BaggageReportDTO> findAll();
    BaggageReportDTO findById(Long id);
    List<BaggageReportDTO> findByPassengerId(Long passengerId);
    BaggageReportDTO save(BaggageReportDTO dto);
    BaggageReportDTO updateStatus(Long id, BaggageStatus status);
    void delete(Long id);
}