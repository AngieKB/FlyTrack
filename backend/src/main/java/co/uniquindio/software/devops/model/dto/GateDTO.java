package co.uniquindio.software.devops.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GateDTO {
    private Long id;

    @NotBlank(message = "El código de puerta es obligatorio")
    private String gateCode;

    @NotBlank(message = "El terminal es obligatorio")
    private String terminal;

    private boolean available;
}