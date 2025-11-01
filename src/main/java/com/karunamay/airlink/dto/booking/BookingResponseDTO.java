package com.karunamay.airlink.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.model.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDTO {
    private Long id;
    private String pnrCode;
    private Long userId;
    private Long flightId;
    private BigDecimal totalAmount;
    private Integer passengerCount;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private List<PassengerResponseDTO> passengers;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

}
