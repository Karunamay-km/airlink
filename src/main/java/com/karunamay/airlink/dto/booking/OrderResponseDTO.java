package com.karunamay.airlink.dto.booking;

import com.karunamay.airlink.model.payment.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponseDTO {

    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long bookingId;

    @NotNull
    private Long totalAmount;

    @NotNull
    private PaymentStatus paymentStatus;

    @NotNull
    private String customerName;

    @NotNull
    private String customerEmail;

    @NotNull
    private String addressLine1;

    private String addressLine2;

    @NotNull
    private String city;

    @NotNull
    private String state;

    @NotNull
    private String pinCode;

    @NotNull
    private String sessionId;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;
}

