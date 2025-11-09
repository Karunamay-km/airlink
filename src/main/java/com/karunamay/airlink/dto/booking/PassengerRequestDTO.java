package com.karunamay.airlink.dto.booking;

import com.karunamay.airlink.model.booking.Gender;
import com.karunamay.airlink.model.booking.Suffix;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PassengerRequestDTO {

    private Long bookingId;

    private Long id;

    @NotNull(message = "Date of Birth is required")
    @Past(message = "Date of Birth must be in the past")
    private LocalDate dob;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Seat ID for assignment is required")
    private Long seatId;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Suffix is required")
    private Suffix suffix;

    @NotBlank(message = "Valid govt id number is required")
    private String govtIdNo;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Checked bag count is required")
    private Integer checkedBagCount;
}
