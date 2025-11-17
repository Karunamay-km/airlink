package com.karunamay.airlink.controller.booking;

import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.payment.PaymentStatus;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.service.BaseService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/webhook")
public class StripeWebhookHandler {

    private final BookingRepository bookingRepository;
    private final BaseService baseService;

//    @Value("${app.stripe.webhookSecret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String signature) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        Optional<StripeObject> paymentIntentObject = event.getDataObjectDeserializer().getObject();

        if (paymentIntentObject.isPresent()) {
            PaymentIntent paymentIntent = (PaymentIntent) paymentIntentObject.get();
            String bookingId = paymentIntent.getMetadata().get("bookingId");
            Booking booking = baseService.findByIdOrThrow(Long.valueOf(bookingId), bookingRepository);

            if ("payment_intent.succeeded".equals(event.getType())) {

                booking.setPaymentStatus(PaymentStatus.PAID);
                booking.setPaymentId(paymentIntent.getId());

                bookingRepository.save(booking);

            } else if ("payment_intent.payment_failed".equals(event.getType())) {
                booking.setPaymentStatus(PaymentStatus.FAILED);
                bookingRepository.save(booking);
            }
            return ResponseEntity.ok("");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
