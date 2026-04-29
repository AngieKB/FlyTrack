package co.uniquindio.software.devops.service.implementation;

import co.uniquindio.software.devops.model.dto.NotificationDTO;
import co.uniquindio.software.devops.model.entity.*;
import co.uniquindio.software.devops.exception.ResourceNotFoundException;
import co.uniquindio.software.devops.repository.FlightRepository;
import co.uniquindio.software.devops.repository.NotificationRepository;
import co.uniquindio.software.devops.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final FlightRepository flightRepository;

    @Override
    public List<NotificationDTO> findAll() {
        return notificationRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public NotificationDTO findById(Long id) {
        return toDTO(notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id)));
    }

    @Override
    public List<NotificationDTO> findByFlightId(Long flightId) {
        return notificationRepository.findByFlightId(flightId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public NotificationDTO save(NotificationDTO dto) {
        Notification n = toEntity(dto);
        n.setSentAt(LocalDateTime.now());
        n.setRead(false);
        return toDTO(notificationRepository.save(n));
    }

    @Override
    public NotificationDTO markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));
        n.setRead(true);
        return toDTO(notificationRepository.save(n));
    }

    @Override
    public void delete(Long id) {
        if (!notificationRepository.existsById(id))
            throw new ResourceNotFoundException("Notificación no encontrada con id: " + id);
        notificationRepository.deleteById(id);
    }

    private NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder().id(n.getId()).message(n.getMessage())
                .type(n.getType()).sentAt(n.getSentAt()).read(n.isRead())
                .flightId(n.getFlight() != null ? n.getFlight().getId() : null).build();
    }

    private Notification toEntity(NotificationDTO dto) {
        Notification.NotificationBuilder builder = Notification.builder()
                .message(dto.getMessage()).type(dto.getType());
        if (dto.getFlightId() != null) {
            Flight flight = flightRepository.findById(dto.getFlightId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado"));
            builder.flight(flight);
        }
        return builder.build();
    }
}