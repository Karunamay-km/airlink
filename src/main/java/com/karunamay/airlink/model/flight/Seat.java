package com.karunamay.airlink.model.flight;

import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.booking.Passenger;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats",
        indexes = {
                @Index(name = "idx_seat_flight", columnList = "flight_id"),
                @Index(name = "idx_seat_number", columnList = "flight_id, seat_no"),
                @Index(name = "idx_seat_availability", columnList = "flight_id, is_available, seat_class")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_flight_seat_no", columnNames = {"flight_id", "seat_no"})
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"flight", "booking", "passenger"})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotNull(message = "Flight is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @NotBlank(message = "Seat number is required")
    @Size(min = 2, max = 4, message = "Seat number must be between 2 and 4 characters (e.g., 1A, 34C)")
    @Column(name = "seat_no", nullable = false, length = 4)
    private String seatNo;

    @NotNull(message = "Seat class is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_class", nullable = false, length = 20)
    private SeatClass seatClass;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean available = true;

    @NotNull(message = "Price modifier is required")
    @DecimalMin(value = "0.00", message = "Price modifier cannot be negative")
    @Column(name = "price_modifier", nullable = false, precision = 3, scale = 2)
    private BigDecimal priceModifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToOne(mappedBy = "seat")
    private Passenger passenger;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}