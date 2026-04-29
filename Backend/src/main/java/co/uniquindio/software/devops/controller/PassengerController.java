package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.PassengerDTO;
import co.uniquindio.software.devops.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public ResponseEntity<List<PassengerDTO>> findAll() {
        return ResponseEntity.ok(passengerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.findById(id));
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<PassengerDTO>> findByFlight(@PathVariable Long flightId) {
        return ResponseEntity.ok(passengerService.findByFlightId(flightId));
    }

    @PostMapping
    public ResponseEntity<PassengerDTO> save(@Valid @RequestBody PassengerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passengerService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerDTO> update(@PathVariable Long id, @Valid @RequestBody PassengerDTO dto) {
        return ResponseEntity.ok(passengerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}