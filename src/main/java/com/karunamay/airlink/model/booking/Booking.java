package com.karunamay.airlink.model.booking;

import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.model.flight.Seat;
import com.karunamay.airlink.model.payment.Order;
import com.karunamay.airlink.model.payment.PaymentStatus;
import com.karunamay.airlink.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookings",
        indexes = {
                @Index(name = "idx_booking_user", columnList = "user_id"),
                @Index(name = "idx_booking_flight", columnList = "flight_id"),
                @Index(name = "idx_booking_status", columnList = "booking_status, payment_status")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"user", "flight", "seats", "passengers", "order"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Flight is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "At least one passenger must be booked")
    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount;

    @NotNull(message = "Booking status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false, length = 20)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "pnr_code", nullable = false, unique = true, length = 10)
    @NotNull(message = "PNR code is required")
    private String pnrCode;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "booking", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false)
    private Set<Seat> seats = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "booking", orphanRemoval = true)
    private Set<Passenger> passengers = new HashSet<>();

    @OneToOne(mappedBy = "booking", fetch = FetchType.LAZY)
    private Order order;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getFlightRoute() {
        return "Flight from " +
                this.getFlight().getSrcAirport().getCity() +
                " to " +
                this.getFlight().getDestAirport().getCity();
    }

    public String getFlightDescription() {
        String departure = this.getFlight().getDepartureTime()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        StringBuilder description = new StringBuilder()
                .append("Flight Booking Details:\n")
                .append("• Route: ")
                .append(this.getFlight().getSrcAirport().getCity())
                .append(" → ")
                .append(this.getFlight().getDestAirport().getCity())
                .append("\n• Flight No: ")
                .append(this.getFlight().getFlightNo())
                .append("\n• From: ")
                .append(this.getFlight().getSrcAirport().getCode())
                .append(" | To: ")
                .append(this.getFlight().getDestAirport().getCode())
                .append("\n• Departure: ")
                .append(departure)
                .append("\n• Passengers: ")
                .append(this.getPassengerCount())
                .append("\n• PNR: ")
                .append(this.getPnrCode())
                .append("\n• Booking ID: ")
                .append(this.getId());
        return description.toString();
    }

    public void addPassenger(Passenger passenger) {
        this.passengers.add(passenger);
        passenger.setBooking(this);
    }

    public void removePassenger(Passenger passenger) {
        this.passengers.remove(passenger);
        passenger.setBooking(null);
    }

    public void addSeat(Seat seat) {
        this.seats.add(seat);
        seat.setBooking(this);
        seat.setAvailable(false);
    }

    public void removeSeat(Seat seat) {
        this.seats.remove(seat);
        seat.setBooking(null);
        seat.setAvailable(true);
    }
}