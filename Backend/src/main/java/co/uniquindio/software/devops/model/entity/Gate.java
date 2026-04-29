package co.uniquindio.software.devops.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "gates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Gate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String gateCode;

    @Column(nullable = false)
    private String terminal;

    private boolean available;

    @OneToMany(mappedBy = "gate")
    private List<Flight> flights;
}