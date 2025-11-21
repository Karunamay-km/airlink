package com.karunamay.airlink.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.dto.flight.SeatResponseDTO;
import com.karunamay.airlink.model.booking.Gender;
import com.karunamay.airlink.model.booking.Suffix;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassengerResponseDTO {

    @NotNull
    private Long id;

    @NotNull
    private Long bookingId;

    @NotNull
    private Gender gender;

    @NotNull
    private SeatResponseDTO seat;

    @NotNull
    private String firstName;

    @NotNull
    private String middleName;

    @NotNull
    private String lastName;

    @NotNull
    private Suffix suffix;

    @NotNull
    private String govtIdNo;

    @NotNull
    private String email;

    @NotNull
    private String phone;

    @NotNull
    private Integer checkedBagCount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate dob;

}
