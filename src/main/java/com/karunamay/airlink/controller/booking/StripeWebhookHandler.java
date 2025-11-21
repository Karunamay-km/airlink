package com.karunamay.airlink.controller.booking;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.service.payment.StripePaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/webhook")
public class StripeWebhookHandler {

    private final StripePaymentService stripePaymentService;

    @PostMapping("/handle-stripe-checkout")
    public ResponseEntity<RestApiResponse<String>> handleStripeWebhook(@RequestBody String payload,
                                                                       @RequestHeader("Stripe-Signature") String signature) throws StripeException {
        return ResponseEntity.ok(
                RestApiResponse.success(stripePaymentService.handleStripeWebhook(payload, signature))
        );
    }
}

