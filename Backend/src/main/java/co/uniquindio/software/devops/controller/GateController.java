package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.GateDTO;
import co.uniquindio.software.devops.service.GateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gates")
@RequiredArgsConstructor
public class GateController {

    private final GateService gateService;

    @GetMapping
    public ResponseEntity<List<GateDTO>> findAll() {
        return ResponseEntity.ok(gateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GateDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(gateService.findById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<GateDTO>> findAvailable() {
        return ResponseEntity.ok(gateService.findAvailable());
    }

    @PostMapping
    public ResponseEntity<GateDTO> save(@Valid @RequestBody GateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gateService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GateDTO> update(@PathVariable Long id, @Valid @RequestBody GateDTO dto) {
        return ResponseEntity.ok(gateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}