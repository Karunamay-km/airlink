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
import com.stripe.param.checkout.SessionRetrieveParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StripePaymentService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BaseService baseService;

    @Value("${app.stripe.secretKey}")
    private String stripeApiKey;

    @Value("${app.stripe.successUrl}")
    private String successUrl;

    @Value("${app.stripe.cancelUrl}")
    private String cancelUrl;

    @Value("${app.stripe.webhookSecret}")
    private String webhookSecret;

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
                .addAllExpand(List.of("customer", "payment_intent"))
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
        log.info("WEBHOOK: Started processing for received Stripe event.");

        Event event;

        try {
            log.debug("WEBHOOK: Attempting to construct event from payload and signature...");
            event = Webhook.constructEvent(payload, signature, webhookSecret);
            log.info("WEBHOOK: Successfully constructed event. ID: {}, Type: {}", event.getId(), event.getType());
        } catch (Exception e) {
            log.warn("WEBHOOK: Invalid signature/payload. Returning 400. Error: {}", e.getMessage());
            return "Invalid signature";
        }

        Optional<StripeObject> eventDeserializeObject = event.getDataObjectDeserializer().getObject();

        try {
            if (eventDeserializeObject.isPresent()) {
                StripeObject stripeObject = eventDeserializeObject.get();

                if (stripeObject instanceof Session sessionObject) {
                    log.info("WEBHOOK: Handling Checkout Session event. Session ID: {}, Event Type: {}", sessionObject.getId(), event.getType());

                    SessionRetrieveParams params = SessionRetrieveParams.builder()
                            .addExpand("customer")
                            .addExpand("payment_intent")
                            .addExpand("payment_intent.latest_charge")
                            .addExpand("payment_intent.latest_charge.billing_details")
                            .addExpand("customer")
                            .addExpand("customer.address")
                            .addExpand("customer.shipping")
                            .addExpand("customer.tax")
                            // If you need payment method details
                            .addExpand("customer.default_source")
                            .addExpand("customer.invoice_settings.default_payment_method")
                            .build();


                    Session session = Session.retrieve(sessionObject.getId(), params, null);

                    switch (event.getType()) {
                        case "checkout.session.completed": {
                            log.info("WEBHOOK: Matched event type 'checkout.session.completed'. Calling handleSessionCompleted...");
                            handleSessionCompleted(session);
                            break;
                        }
                        case "checkout.session.async_payment_succeeded": {
                            log.info("WEBHOOK: Matched event type 'checkout.session.async_payment_succeeded'. Calling handleAsyncPaymentSucceeded...");
                            handleAsyncPaymentSucceeded(session);
                            break;
                        }
                        case "checkout.session.async_payment_failed",
                             "checkout.session.expired": {
                            log.warn("WEBHOOK: Matched event type '{}'. Calling handleAsyncPaymentFailed...", event.getType());
                            handleAsyncPaymentFailed(session);
                            break;
                        }
                        default: {
                            log.info("WEBHOOK: Unhandled event type: {}. Skipping processing.", event.getType());
                            break;
                        }
                    }
                } else {
                    log.warn("WEBHOOK: Deserialized object is not a Session. Skipping processing");
                }
            } else {
                log.warn("WEBHOOK: Could not deserialize event data object. Skipping processing.");
            }
        } catch (StripeException e) {
            throw new BusinessException("Payment failed. Please try again");
        }
        log.info("WEBHOOK: Finished processing for event ID: {}", event.getId());
        return "";
    }

    private void handleSessionCompleted(Session session) {
        log.info("COMPLETED_HANDLER: Starting for Session ID: {}", session.getId());

        String bookingId = session.getMetadata().get("bookingId");
        String userId = session.getMetadata().get("userId");

        log.debug("COMPLETED_HANDLER: Extracted metadata. bookingId: {}, userId: {}", bookingId, userId);

        if (bookingId == null || userId == null) {
            log.error("COMPLETED_HANDLER: Missing required metadata ('bookingId' or 'userId'). Aborting.");
            return;
        }

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> {
                    log.error("COMPLETED_HANDLER: User with ID {} not found for session {}", userId, session.getId());
                    return new BusinessException("Invalid or unknown user with this session");
                });
        log.info("COMPLETED_HANDLER: User (ID: {}) found.", user.getId());

        Booking booking = bookingRepository.findById(Long.valueOf(bookingId))
                .orElseThrow(() -> {
                    log.error("COMPLETED_HANDLER: Booking with ID {} not found for session {}", bookingId, session.getId());
                    return new BusinessException("Invalid or unknown booking with this session");
                });
        log.info("COMPLETED_HANDLER: Booking (ID: {}) found.", booking.getId());

        Customer customer = session.getCustomerObject();
        PaymentIntent pi = session.getPaymentIntentObject();

        log.debug("COMPLETED_HANDLER: Retrieved Customer ({}) and PaymentIntent ({}).", customer.getId(), pi.getId());

        Address address = pi.getLatestChargeObject().getBillingDetails().getAddress();
        log.debug("COMPLETED_HANDLER: Retrieved billing address for charge {}.", pi.getLatestChargeObject().getId());

        Order order = orderRepository
                .findBySessionId(session.getId())
                .orElseGet(() -> {
                    log.info("COMPLETED_HANDLER: Order not found for session {}. Creating new order.", session.getId());
                    return Order.builder()
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
                            .totalAmount(booking.getTotalAmount())
                            .build();
                });

        booking.setOrder(order);
        log.debug("COMPLETED_HANDLER: Attached order to booking. Stripe payment status: {}", session.getPaymentStatus());

        if (session.getPaymentStatus().equals("paid")) {
            log.info("COMPLETED_HANDLER: Payment is 'paid'. Setting BookingStatus to CONFIRMED and PaymentStatus to PAID.");
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            booking.setPaymentStatus(PaymentStatus.PAID);
            order.setPaymentStatus(PaymentStatus.PAID);
        } else if (session.getPaymentStatus().equals("unpaid")) {
            log.warn("COMPLETED_HANDLER: Payment is 'unpaid'. Setting BookingStatus to PROCESSING and PaymentStatus to IN_PROGRESS.");
            booking.setBookingStatus(BookingStatus.PROCESSING);
            booking.setPaymentStatus(PaymentStatus.IN_PROGRESS);
            order.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        }

        log.info("COMPLETED_HANDLER: Saving updated Order and Booking.");
        orderRepository.save(order);
        bookingRepository.save(booking);
        log.info("COMPLETED_HANDLER: Successfully processed Session ID: {}", session.getId());
    }

    private void handleAsyncPaymentSucceeded(Session session) {
        log.info("ASYNC_SUCCESS_HANDLER: Starting for Session ID: {}", session.getId());

        String bookingId = session.getMetadata().get("bookingId");
        if (bookingId == null) {
            log.error("ASYNC_SUCCESS_HANDLER: Missing required metadata 'bookingId'. Aborting.");
            return;
        }
        log.debug("ASYNC_SUCCESS_HANDLER: Extracted bookingId: {}", bookingId);

        Booking booking = baseService.findByIdOrThrow(Long.valueOf(bookingId), bookingRepository);
        Order order = booking.getOrder();

        log.info("ASYNC_SUCCESS_HANDLER: Booking (ID: {}) found. Updating statuses to CONFIRMED/PAID.", booking.getId());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.PAID);
        order.setPaymentStatus(PaymentStatus.PAID);

        log.info("ASYNC_SUCCESS_HANDLER: Saving updated Booking and Order.");
        bookingRepository.save(booking);
        orderRepository.save(order);

        log.info("ASYNC_SUCCESS_HANDLER: Successfully processed Session ID: {}", session.getId());
    }

    private void handleAsyncPaymentFailed(Session session) {
        log.warn("ASYNC_FAIL_HANDLER: Starting for Session ID: {}", session.getId());

        String bookingId = session.getMetadata().get("bookingId");
        if (bookingId == null) {
            log.error("ASYNC_FAIL_HANDLER: Missing required metadata 'bookingId'. Aborting.");
            return;
        }
        log.debug("ASYNC_FAIL_HANDLER: Extracted bookingId: {}", bookingId);

        Booking booking = baseService.findByIdOrThrow(Long.valueOf(bookingId), bookingRepository);
        Order order = booking.getOrder();

        log.warn("ASYNC_FAIL_HANDLER: Booking (ID: {}) found. Updating statuses to PENDING/FAILED.", booking.getId());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.FAILED);
        order.setPaymentStatus(PaymentStatus.FAILED);

        log.warn("ASYNC_FAIL_HANDLER: Saving updated Booking and Order.");
        bookingRepository.save(booking);
        orderRepository.save(order);

        log.warn("ASYNC_FAIL_HANDLER: Finished processing for Session ID: {}", session.getId());
    }

}
