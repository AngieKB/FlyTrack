package co.uniquindio.software.devops.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "baggage_reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BaggageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private BaggageStatus status;

    private LocalDateTime reportedAt;

    private String baggageTag;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
}