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
@Table(name = "airlines",
        indexes = {
                @Index(name = "idx_airline_name", columnList = "name"),
                @Index(name = "idx_airline_country", columnList = "country")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_airline_code", columnNames = "code"),
                @UniqueConstraint(name = "uk_airline_name", columnNames = "name")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"aircrafts", "operatedFlights"})
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotBlank(message = "Code is required")
    @Size(min = 2, max = 2, message = "Code must be 2 characters only")
    @Column(name = "code", nullable = false, unique = true, length = 2)
    private String code;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 60, message = "Name must be between 3 and 60 characters")
    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Size(min = 3, max = 40, message = "Country must be between 3 and 40 characters")
    @Column(name = "country", nullable = false, length = 40)
    @Builder.Default
    private String country = "India";

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "airline"
    )
    @Builder.Default
    private Set<Aircraft> aircrafts = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "airline"
    )
    @Builder.Default
    private Set<Flight> operatedFlights = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addAircraft(Aircraft aircraft) {
        this.aircrafts.add(aircraft);
        aircraft.setAirline(this);
    }

    public void removeAircraft(Aircraft aircraft) {
        this.aircrafts.remove(aircraft);
    }

    public void addOperatedFlight(Flight flight) {
        this.operatedFlights.add(flight);
        flight.setAirline(this);
    }

    public void removeOperatedFlight(Flight flight) {
        this.operatedFlights.remove(flight);
    }
}
