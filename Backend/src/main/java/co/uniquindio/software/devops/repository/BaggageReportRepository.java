package co.uniquindio.software.devops.repository;

import co.uniquindio.software.devops.model.entity.BaggageReport;
import co.uniquindio.software.devops.model.entity.BaggageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BaggageReportRepository extends JpaRepository<BaggageReport, Long> {
    List<BaggageReport> findByPassengerId(Long passengerId);
    List<BaggageReport> findByStatus(BaggageStatus status);
}