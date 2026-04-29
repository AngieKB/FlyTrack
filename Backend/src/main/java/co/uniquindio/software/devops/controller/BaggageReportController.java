package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.BaggageReportDTO;
import co.uniquindio.software.devops.model.entity.BaggageStatus;
import co.uniquindio.software.devops.service.BaggageReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/baggage-reports")
@RequiredArgsConstructor
public class BaggageReportController {

    private final BaggageReportService baggageReportService;

    @GetMapping
    public ResponseEntity<List<BaggageReportDTO>> findAll() {
        return ResponseEntity.ok(baggageReportService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaggageReportDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(baggageReportService.findById(id));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<BaggageReportDTO>> findByPassenger(@PathVariable Long passengerId) {
        return ResponseEntity.ok(baggageReportService.findByPassengerId(passengerId));
    }

    @PostMapping
    public ResponseEntity<BaggageReportDTO> save(@Valid @RequestBody BaggageReportDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(baggageReportService.save(dto));
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<BaggageReportDTO> updateStatus(@PathVariable Long id, @PathVariable BaggageStatus status) {
        return ResponseEntity.ok(baggageReportService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        baggageReportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}