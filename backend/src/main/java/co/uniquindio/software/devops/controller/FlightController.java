package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.FlightDTO;
import co.uniquindio.software.devops.model.entity.FlightStatus;
import co.uniquindio.software.devops.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<List<FlightDTO>> findAll() {
        return ResponseEntity.ok(flightService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.findById(id));
    }

    @GetMapping("/number/{flightNumber}")
    public ResponseEntity<FlightDTO> findByFlightNumber(@PathVariable String flightNumber) {
        return ResponseEntity.ok(flightService.findByFlightNumber(flightNumber));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FlightDTO>> findByStatus(@PathVariable FlightStatus status) {
        return ResponseEntity.ok(flightService.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<FlightDTO> save(@Valid @RequestBody FlightDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDTO> update(@PathVariable Long id, @Valid @RequestBody FlightDTO dto) {
        return ResponseEntity.ok(flightService.update(id, dto));
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<FlightDTO> updateStatus(@PathVariable Long id, @PathVariable FlightStatus status) {
        return ResponseEntity.ok(flightService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }
}