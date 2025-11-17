package com.karunamay.airlink.controller.booking;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.booking.PaymentInitRequestDTO;
import com.karunamay.airlink.dto.booking.PaymentInitResponseDTO;
import com.karunamay.airlink.dto.booking.PaymentResponseDTO;
import com.karunamay.airlink.dto.booking.PaymentStatusCheckRequestDTO;
import com.karunamay.airlink.dto.error.ErrorResponseDTO;
import com.karunamay.airlink.service.payment.PaymentService;
import com.karunamay.airlink.service.payment.StripePaymentService;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/payment")
@Tag(name = "Payment", description = "Payment handler for bookings.")
public class PaymentController {

    private final StripePaymentService stripePaymentService;
    private final PaymentService paymentService;

    @Operation(summary = "Initialize payment for bookings")
    @ApiResponse(
            responseCode = "200",
            description = "Payment has initialize successfully.",
            content = @Content(
                    schema = @Schema(implementation = BasePaymentResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error while processing payment request.",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PostMapping("/init/{bookingId}")
    @PreAuthorize(
            "hasRole('ADMIN') or @SecurityService.isOwnerOfTheBooking(#bookingId, authentication)"
    )
    public ResponseEntity<RestApiResponse<PaymentInitResponseDTO>> initPayment(
            @Parameter(
                    description = "The id of the booking"
            ) @PathVariable Long bookingId,
            @Valid @RequestBody PaymentInitRequestDTO payload
    ) {
        Session session = stripePaymentService.createPaymentSession(payload);
        return ResponseEntity.ok(
                RestApiResponse.success(
                        PaymentInitResponseDTO.builder().url(session.getUrl()).build()
                )
        );
    }

    @GetMapping("/process")
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<RestApiResponse<PaymentResponseDTO>> processPayment(
            @RequestParam String sessionId
    ) {
        return ResponseEntity.ok(RestApiResponse.success(paymentService.processPayment(sessionId)));
    }

    @GetMapping("/check-status")
    @PreAuthorize("hasRole('ADMIN') or @SecurityService.isOwnerOfTheOrder(#payload.orderId, authentication)")
    public ResponseEntity<RestApiResponse<PaymentResponseDTO>> checkPaymentStatus(
            @Valid @RequestBody PaymentStatusCheckRequestDTO payload
    ) {
        return ResponseEntity.ok(RestApiResponse.success(paymentService.checkPaymentStatus(payload.getOrderId())));
    }

    private static class BasePaymentResponseDTO
            extends RestApiResponse<PaymentInitResponseDTO> {
    }


}
