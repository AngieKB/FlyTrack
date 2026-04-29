package co.uniquindio.software.devops.service;

import co.uniquindio.software.devops.model.dto.NotificationDTO;
import java.util.List;

public interface NotificationService {
    List<NotificationDTO> findAll();
    NotificationDTO findById(Long id);
    List<NotificationDTO> findByFlightId(Long flightId);
    NotificationDTO save(NotificationDTO dto);
    NotificationDTO markAsRead(Long id);
    void delete(Long id);
}