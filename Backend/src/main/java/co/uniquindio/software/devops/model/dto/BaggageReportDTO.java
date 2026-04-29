package co.uniquindio.software.devops.model.dto;

import co.uniquindio.software.devops.model.entity.BaggageStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BaggageReportDTO {
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    private BaggageStatus status;
    private LocalDateTime reportedAt;
    private String baggageTag;
    private Long passengerId;
}