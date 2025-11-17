package com.karunamay.airlink.model.payment;

import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_user_id", columnList = "user_id"),
                @Index(name = "idx_order_booking_id", columnList = "booking_id")
        })
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NotNull(message = "Customer name is required")
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @NotNull(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Column(name = "customer_email", nullable = false, length = 120)
    private String customerEmail;

    @NotNull(message = "Address Line 1 is required")
    @Column(name = "address_line_1", nullable = false, length = 200)
    private String addressLine1;

    @Column(name = "address_line_2", length = 200)
    private String addressLine2;

    @NotNull(message = "City is required")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotNull(message = "State is required")
    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @NotNull(message = "PIN code is required")
    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "Invalid PIN code format"
    )
    @Column(name = "pin_code", nullable = false, length = 6)
    private String pinCode;

    @NotNull(message = "Session ID is required")
    @Column(name = "session_id", nullable = false, unique = true, length = 200)
    private String sessionId;

    @NotNull(message = "User id is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @NotNull(message = "Booking is is required")
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "booking_id", nullable = false)
    @ToString.Exclude
    private Booking booking;

    @NotNull(message = "Total amount is required")
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
