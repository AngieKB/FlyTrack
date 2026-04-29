package co.uniquindio.software.devops.model.dto;

import co.uniquindio.software.devops.model.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDTO {
    private Long id;

    @NotBlank(message = "El mensaje es obligatorio")
    private String message;

    private NotificationType type;
    private LocalDateTime sentAt;
    private boolean read;
    private Long flightId;
}