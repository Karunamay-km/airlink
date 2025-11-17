package com.karunamay.airlink.service.payment;

import com.karunamay.airlink.dto.booking.PaymentResponseDTO;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.model.booking.Booking;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.payment.Order;
import com.karunamay.airlink.model.payment.PaymentStatus;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.booking.BookingRepository;
import com.karunamay.airlink.repository.order.OrderRepository;
import com.karunamay.airlink.repository.user.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Address;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Value("${app.stripe.secretKey}")
    private String stripeApiKey;

    @Override
    public PaymentResponseDTO processPayment(String sessionId) {

        Stripe.apiKey = stripeApiKey;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("expand", Arrays.asList("customer", "payment_intent", "line_items"));

            Session session = Session.retrieve(sessionId, params, RequestOptions.getDefault());

            Map<String, String> metaData = session.getMetadata();

            User user = userRepository.findById(Long.valueOf(metaData.get("userId"))).get();
            Booking booking = bookingRepository.findById(Long.valueOf(metaData.get("bookingId"))).get();

            Customer customer = session.getCustomerObject();
            PaymentIntent pi = session.getPaymentIntentObject();

            Address address = pi.getLatestChargeObject().getBillingDetails().getAddress();

            String paymentStatus = pi.getStatus();

            Order order = Order.builder()
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
                    .totalAmount(Long.valueOf(metaData.get("totalAmount")))
                    .build();

            PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder().build();

            checkStatus(paymentStatus, order, paymentResponseDTO);

            return paymentResponseDTO;

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PaymentResponseDTO checkPaymentStatus(Long orderId) {
        Stripe.apiKey = stripeApiKey;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("expand", Arrays.asList("payment_intent"));

            Order order = orderRepository
                    .findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

            Session session = Session.retrieve(order.getSessionId(), params, RequestOptions.getDefault());

            String paymentStatus = session.getPaymentStatus();

            PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder().build();

            checkStatus(paymentStatus, order, paymentResponseDTO);

            return paymentResponseDTO;

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkStatus(String paymentStatus, Order order, PaymentResponseDTO paymentResponseDTO) {
        if (paymentStatus != null) {
            switch (paymentStatus) {
                case "succeeded":
                    order.setPaymentStatus(PaymentStatus.PAID);
                    order.getBooking().setBookingStatus(BookingStatus.CONFIRMED);
                    order.getBooking().setPaymentStatus(PaymentStatus.PAID);
                    paymentResponseDTO.setMessage("Payment has successful. Your booking has been confirmed.");
                    paymentResponseDTO.setOrder(order);
                    break;
                case "requires_payment_method":
                case "canceled":
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    order.getBooking().setBookingStatus(BookingStatus.PENDING);
                    order.getBooking().setPaymentStatus(PaymentStatus.FAILED);
                    paymentResponseDTO.setMessage("Payment has failed. Please try again after some time.");
                    paymentResponseDTO.setOrder(order);
                    break;
                default:
                    order.setPaymentStatus(PaymentStatus.PENDING);
                    order.getBooking().setBookingStatus(BookingStatus.PENDING);
                    order.getBooking().setPaymentStatus(PaymentStatus.FAILED);
                    paymentResponseDTO.setMessage("Payment is processing. Your booking will confirm shortly once payment has completed.");
                    paymentResponseDTO.setOrder(order);
                    break;
            }
        }
    }

}
