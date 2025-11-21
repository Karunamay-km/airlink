package com.karunamay.airlink.controller.booking;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.booking.OrderRequestDTO;
import com.karunamay.airlink.dto.booking.OrderResponseDTO;
import com.karunamay.airlink.dto.error.ErrorResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.model.payment.PaymentStatus;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.service.booking.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Operations related to creating and managing payment orders.")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create a new Order",
            description = "Creates an order for a booking with payment details."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = BaseOrderResponseDTO.class))
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderRequestDTO requestDTO
    ) {
        log.info("REST: Create order request received for bookingId={} and userId={}",
                requestDTO.getBookingId(),
                requestDTO.getUserId());

        OrderResponseDTO response = orderService.createOrder(requestDTO);

        log.info("REST: Order created successfully with id={}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestApiResponse.success(response));
    }

    @Operation(
            summary = "Update an order",
            description = "Updates an order's details by ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Order updated successfully",
            content = @Content(schema = @Schema(implementation = BaseOrderResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<OrderResponseDTO>> updateOrder(
            @Parameter(description = "The ID of the order to update")
            @PathVariable Long id,
            @Valid @RequestBody OrderRequestDTO requestDTO
    ) {
        log.info("REST: Update request for order id={}", id);

        OrderResponseDTO response = orderService.updateOrder(id, requestDTO);

        log.info("REST: Order updated successfully id={}", id);

        return ResponseEntity.ok(RestApiResponse.success(response));
    }

    @Operation(
            summary = "Get order by ID",
            description = "Retrieves an order by its primary key ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Order retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseOrderResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @SecurityService.isOwnerOfOrder(#id, authentication)")
    public ResponseEntity<RestApiResponse<OrderResponseDTO>> getOrderById(
            @Parameter(description = "The unique ID of the order")
            @PathVariable Long id
    ) {
        log.info("REST: Fetch order by id={}", id);

        OrderResponseDTO response = orderService.getOrderById(id);

        log.info("REST: Order fetched successfully id={}", id);

        return ResponseEntity.ok(RestApiResponse.success(response));
    }

    @Operation(
            summary = "Get order by booking ID",
            description = "Retrieves the order associated with a specific booking."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Retrieves orders by booking Id",
            content = @Content(schema = @Schema(implementation = BaseOrderResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/search/by-booking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<OrderResponseDTO>> getOrderByBookingId(
            @RequestParam Long bookingId
    ) {
        log.info("REST: Fetch order for bookingId={}", bookingId);

        OrderResponseDTO response = orderService.getOrderByBookingId(bookingId);

        log.info("REST: Order for bookingId={} fetched successfully", bookingId);

        return ResponseEntity.ok(RestApiResponse.success(response));
    }

    @Operation(
            summary = "Get orders of authenticated user",
            description = "Returns paginated orders belonging to the logged-in user."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Retrieves orders of the current user",
            content = @Content(schema = @Schema(implementation = PaginationOrderResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<PageResponseDTO<OrderResponseDTO>>> getOrdersForUser(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("REST: Fetch orders for user {}", user.getUsername());

        Pageable pageable = PageRequest.of(page, size, direction, sortBy);

        return ResponseEntity.ok(
                RestApiResponse.success(orderService.getOrdersByUserId(user.getId(), pageable))
        );
    }

    @Operation(
            summary = "Get orders by payment status",
            description = "Fetches paginated orders filtered by status (e.g., SUCCESS, FAILED, PENDING)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Retrieves orders by payment status",
            content = @Content(schema = @Schema(implementation = PaginationOrderResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @GetMapping("/search/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<PageResponseDTO<OrderResponseDTO>>> getOrdersByPaymentStatus(
            @RequestParam PaymentStatus paymentStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("REST: Fetch orders with paymentStatus={}", paymentStatus);

        Pageable pageable = PageRequest.of(page, size, direction, sortBy);

        return ResponseEntity.ok(
                RestApiResponse.success(orderService.getOrdersByPaymentStatus(paymentStatus, pageable))
        );
    }

    private static class PaginationOrderResponseDTO
            extends RestApiResponse<PageResponseDTO<OrderResponseDTO>> {
    }

    private static class BaseOrderResponseDTO
            extends RestApiResponse<OrderResponseDTO> {
    }
}
