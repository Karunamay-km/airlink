package com.karunamay.airlink.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.model.booking.Gender;
import com.karunamay.airlink.model.booking.Suffix;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassengerResponseDTO {

    private Long id;
    private Long bookingId;
    private Gender gender;
    private Long seatId;
    private String seatNo;
    private String firstName;
    private String middleName;
    private String lastName;
    private Suffix suffix;
    private String email;
    private String phone;
    private Integer checkedBagCount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
