package com.karunamay.airlink.model.flight;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "airports",
        indexes = {
                @Index(name = "idx_airport_name", columnList = "name"),
                @Index(name = "idx_airport_code", columnList = "code"),
                @Index(name = "idx_airport_city", columnList = "city"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_airport_code", columnNames = "code"),
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"departingFlights", "arrivingFlights"})
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotBlank(message = "Code is required")
    @Size(min = 3, max = 4, message = "Code must be 3 or 4 characters")
    @Column(name = "code", nullable = false, unique = true, length = 4)
    private String code;


    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 40, message = "Name must be between 4 and 40 characters")
    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @NotBlank(message = "City is required")
    @Size(min = 4, max = 40, message = "City must be between 4 and 40 characters")
    @Column(name = "city", nullable = false, length = 40)
    private String city;

    @Size(min = 4, max = 40, message = "Country must be between 4 and 40 characters")
    @Column(name = "country", length = 40)
    @Builder.Default
    private String country = "India";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(
            mappedBy = "srcAirport",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<Flight> departingFlights = new HashSet<>();

    @OneToMany(
            mappedBy = "destAirport",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<Flight> arrivingFlights = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addDepartingFlight(Flight flight) {
        this.departingFlights.add(flight);
        flight.setSrcAirport(this);
    }

    public void removeDepartingFlight(Flight flight) {
        this.departingFlights.remove(flight);
    }

    public void addArrivingFlight(Flight flight) {
        this.arrivingFlights.add(flight);
        flight.setDestAirport(this);
    }

    public void removeArrivingFlight(Flight flight) {
        this.arrivingFlights.remove(flight);
    }
}
