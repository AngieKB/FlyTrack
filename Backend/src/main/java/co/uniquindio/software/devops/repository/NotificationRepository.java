package co.uniquindio.software.devops.repository;

import co.uniquindio.software.devops.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByFlightId(Long flightId);
    List<Notification> findByRead(boolean read);
}