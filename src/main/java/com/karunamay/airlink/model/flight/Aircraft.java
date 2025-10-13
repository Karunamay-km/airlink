package com.karunamay.airlink.model.flight;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "aircrafts",
        indexes = {
                @Index(name = "idx_aircraft_model", columnList = "model"),
                @Index(name = "idx_aircraft_capacity", columnList = "capacity")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"flights", "airline"})
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotBlank(message = "Model is required")
    @Size(min = 3, max = 50, message = "Model name must be between 3 and 50 characters")
    @Column(name = "model", nullable = false, unique = true, length = 50)
    private String model;

    @NotBlank(message = "Registration number is required")
    @Size(min = 5, max = 7, message = "Registration number must be between 5 and 7 characters")
    @Column(name = "registration_number", nullable = false, unique = true, length = 7)
    private String registrationNumber;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_airline_id", nullable = false)
    private Airline airline;

    @OneToMany(
            mappedBy = "aircraft",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<Flight> flights = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addFlight(Flight flight) {
        this.flights.add(flight);
        flight.setAircraft(this);
    }

    public void removeFlight(Flight flight) {
        this.flights.remove(flight);
    }
}
