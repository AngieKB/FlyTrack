package co.uniquindio.software.devops.repository;

import co.uniquindio.software.devops.model.entity.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GateRepository extends JpaRepository<Gate, Long> {
    Optional<Gate> findByGateCode(String gateCode);
    List<Gate> findByAvailable(boolean available);
    List<Gate> findByTerminal(String terminal);
}