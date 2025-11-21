package com.karunamay.airlink.service.payment;

import com.karunamay.airlink.dto.payment.PaymentInitRequestDTO;
import com.karunamay.airlink.exceptions.BusinessException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.payment.Order;
import com.karunamay.airlink.model.payment.PaymentStatus;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.repository.order.OrderRepository;
import com.karunamay.airlink.repository.user.UserRepository;
import com.karunamay.airlink.service.BaseService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StripePaymentService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Value("${app.stripe.secretKey}")
    private String stripeApiKey;

    @Value("${app.stripe.successUrl}")
    private String successUrl;

    @Value("${app.stripe.webhookSecret}")
    private String webhookSecret;

    private final BaseService baseService;

    @Transactional(readOnly = true)
    public Session createPaymentSession(PaymentInitRequestDTO requestDTO) {
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

    @Transactional
    public String handleStripeWebhook(String payload, String signature) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (Exception e) {
            return "Invalid signature";
        }

        Optional<StripeObject> eventDeserializeObject = event.getDataObjectDeserializer().getObject();

        if (eventDeserializeObject.isPresent()) {
            StripeObject stripeObject = eventDeserializeObject.get();

            if (stripeObject instanceof Session session) {
                switch (event.getType()) {
                    case "checkout.session.completed": {
                        handleSessionCompleted(session);
                        break;
                    }
                    case "checkout.session.async_payment_succeeded": {
                        handleAsyncPaymentSucceeded(session);
                        break;
                    }
                    case "checkout.session.async_payment_failed",
                         "checkout.session.expired": {
                        handleAsyncPaymentFailed(session);
                        break;
                    }
                }
            }
        }
        return "";
    }

    private void handleSessionCompleted(Session session) {

        String bookingId = session.getMetadata().get("bookingId");
        String userId = session.getMetadata().get("userId");

        if (bookingId == null || userId == null) return;

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new BusinessException("Invalid or unknown user with this session"));

        Booking booking = bookingRepository.findById(Long.valueOf(bookingId))
                .orElseThrow(() -> new BusinessException("Invalid or unknown booking with this session"));

        Customer customer = session.getCustomerObject();
        PaymentIntent pi = session.getPaymentIntentObject();

        Address address = pi.getLatestChargeObject().getBillingDetails().getAddress();

        Order order = orderRepository
                .findBySessionId(session.getId())
                .orElseGet(() -> Order.builder()
                        .customerName(customer.getName())
                        .customerEmail(customer.getEmail())
                        .addressLine1(address.getLine1())
                        .addressLine2(address.getLine2())
                        .city(address.getCity())
                        .state(address.getState())
                        .pinCode(address.getPostalCode())
                        .sessionId(session.getId())
                        .user(user)
                        .booking(booking)
                        .totalAmount(Long.valueOf(session.getMetadata().get("totalAmount")))
                        .build());

        booking.setOrder(order);

        if (session.getPaymentStatus().equals("paid")) {

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            booking.setPaymentStatus(PaymentStatus.PAID);

            order.setPaymentStatus(PaymentStatus.PAID);

        } else if (session.getPaymentStatus().equals("unpaid")) {

            booking.setBookingStatus(BookingStatus.PROCESSING);
            booking.setPaymentStatus(PaymentStatus.IN_PROGRESS);

            order.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        }

        orderRepository.save(order);
        bookingRepository.save(booking);
    }

    private void handleAsyncPaymentSucceeded(Session session) {
        String bookingId = session.getMetadata().get("bookingId");
        if (bookingId == null) return;

        Booking booking = baseService.findByIdOrThrow(Long.valueOf(bookingId), bookingRepository);
        Order order = booking.getOrder();

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.PAID);

        order.setPaymentStatus(PaymentStatus.PAID);

        bookingRepository.save(booking);

        orderRepository.save(order);

    }

    private void handleAsyncPaymentFailed(Session session) {
        String bookingId = session.getMetadata().get("bookingId");
        if (bookingId == null) return;

        Booking booking = baseService.findByIdOrThrow(Long.valueOf(bookingId), bookingRepository);
        Order order = booking.getOrder();

        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.FAILED);

        order.setPaymentStatus(PaymentStatus.FAILED);

        bookingRepository.save(booking);

        orderRepository.save(order);
    }

}
