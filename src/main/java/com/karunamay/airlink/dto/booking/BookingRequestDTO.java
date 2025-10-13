package com.karunamay.airlink.dto.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingRequestDTO {

    @NotNull(message = "User ID is required for booking")
    private Long userId;

    @NotNull(message = "Flight ID is required for booking")
    private Long flightId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "At least one passenger must be booked")
    private Integer passengerCount;

    @NotEmpty(message = "Booking must contain at least one passenger")
    @Size(min = 1, message = "Passenger list cannot be empty")
    private List<@Valid PassengerRequestDTO> passengerList;

}
