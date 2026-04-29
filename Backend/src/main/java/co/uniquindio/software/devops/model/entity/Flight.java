package co.uniquindio.software.devops.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flights")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String flightNumber;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    private String airline;

    @ManyToOne
    @JoinColumn(name = "gate_id")
    private Gate gate;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Passenger> passengers;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Notification> notifications;
}