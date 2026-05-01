package co.uniquindio.software.devops.model.dto;

import co.uniquindio.software.devops.model.entity.FlightStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FlightDTO {
    private Long id;

    @NotBlank(message = "El número de vuelo es obligatorio")
    private String flightNumber;

    @NotBlank(message = "El origen es obligatorio")
    private String origin;

    @NotBlank(message = "El destino es obligatorio")
    private String destination;

    @NotNull(message = "La hora de salida es obligatoria")
    private LocalDateTime departureTime;

    @NotNull(message = "La hora de llegada es obligatoria")
    private LocalDateTime arrivalTime;

    private FlightStatus status;
    private String airline;
    private Long gateId;
    private String gateCode;
}