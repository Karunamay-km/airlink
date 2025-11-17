package com.karunamay.airlink.service.payment;

import com.karunamay.airlink.dto.booking.PaymentInitRequestDTO;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.repository.user.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    @Value("${app.stripe.secretKey}")
    private String stripeApiKey;
    @Value("${app.stripe.successUrl}")
    private String successUrl;

    public Session createPaymentSession(PaymentInitRequestDTO requestDTO) {
        User user = userRepository
                .findById(requestDTO.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + requestDTO.getUserId() + " not found"
                        )
                );

        Booking booking = bookingRepository
                .findById(requestDTO.getBookingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking with id " +
                                        requestDTO.getBookingId() +
                                        " not found"
                        )
                );

        Long amount = booking.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValueExact();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "/?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(successUrl + "/?session_id={CHECKOUT_SESSION_ID}")
                .setBillingAddressCollection(
                        SessionCreateParams.BillingAddressCollection.REQUIRED
                )
                .setCustomerCreation(SessionCreateParams.CustomerCreation.ALWAYS)
                .setNameCollection(
                        SessionCreateParams.NameCollection.builder()
                                .setIndividual(
                                        SessionCreateParams.NameCollection.Individual.builder()
                                                .setEnabled(true)
                                                .setOptional(false)
                                                .build()
                                )
                                .build()
                )
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("inr")
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(booking.getFlightRoute())
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .putMetadata("userId", requestDTO.getUserId().toString())
                .putMetadata("bookingId", requestDTO.getBookingId().toString())
                .build();

        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Error creating checkout session", e);
        }
    }
}
