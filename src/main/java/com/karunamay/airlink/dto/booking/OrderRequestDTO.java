package com.karunamay.airlink.dto.booking;

import com.karunamay.airlink.model.payment.PaymentStatus;
import com.karunamay.airlink.validation.group.OnCreate;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequestDTO {

    @NotNull(message = "User ID is required", groups = {OnCreate.class})
    private Long userId;

    @NotNull(message = "Booking ID is required", groups = {OnCreate.class})
    private Long bookingId;

    @NotNull(message = "Total amount is required")
    @Min(value = 1, message = "Total amount must be at least 1")
    private BigDecimal totalAmount;

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    @NotNull(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    @NotNull(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotNull(message = "Address Line 1 is required")
    @Size(max = 200, message = "Address Line 1 must not exceed 200 characters")
    private String addressLine1;

    @Size(max = 200, message = "Address Line 2 must not exceed 200 characters")
    private String addressLine2;

    @NotNull(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotNull(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotNull(message = "PIN code is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid PIN code")
    private String pinCode;

    @NotNull(message = "Session ID is required")
    private String sessionId;
}

