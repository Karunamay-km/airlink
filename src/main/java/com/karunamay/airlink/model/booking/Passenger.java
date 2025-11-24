package com.karunamay.airlink.model.booking;

import com.karunamay.airlink.model.flight.Seat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @JoinColumn(
            name = "booking_id"
    )
    private Booking booking;

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 100, message = "First name must be between 3 and 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Builder.Default
    @Size(max = 100, message = "Middle name must be less than 100 characters")
    @Column(name = "middle_name", nullable = false, length = 100)
    private String middleName = "";

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 100, message = "Last name must be between 3 and 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "Suffix is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "suffix", nullable = false, length = 5)
    private Suffix suffix;

    @NotNull(message = "Date of Birth is required")
    @Past(message = "Date of Birth must be in the past")
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @NotNull(message = "Valid govt id number is required")
    @Column(name = "id_number", nullable = false)
    private String govtIdNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Phone is required")
    @Column(name = "phone", nullable = false, length = 100)
    private String phone;

    @Column(name = "checked_bag_count")
    private int checkedBagCount;

    @NotNull(message = "Seat is required")
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
