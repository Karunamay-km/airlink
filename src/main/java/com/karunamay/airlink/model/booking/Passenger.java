package com.karunamay.airlink.model.booking;

import com.karunamay.airlink.model.flight.Seat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "passengers",
        indexes = {
                @Index(name = "idx_passenger_booking", columnList = "booking_id"),
                @Index(name = "idx_passenger_seat", columnList = "seat_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_passenger_seat", columnNames = "seat_id"),
                @UniqueConstraint(name = "uk_booking_person", columnNames = {"booking_id", "full_name", "dob"}
                )
        })

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"booking", "seat"})
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotNull(message = "Date of Birth is required")
    @Past(message = "Date of Birth must be in the past")
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @NotNull(message="Seat is required")
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    private Seat seat;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
