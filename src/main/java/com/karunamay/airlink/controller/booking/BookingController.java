package com.karunamay.airlink.controller.booking;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.booking.BookingRequestDTO;
import com.karunamay.airlink.dto.booking.BookingResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.model.booking.BookingStatus;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.service.booking.BookingService;
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
@RequestMapping("/bookings")
@Tag(name = "Booking", description = "Operations related to managing flight bookings (CRUD and lookup).")
public class BookingController {

    private final BookingService bookingService;


    @Operation(
            summary = "Create a new flight booking",
            description = "Initiates a new booking with passenger and flight details. Automatically generates a PNR code."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Booking created successfully",
            content = @Content(
                    schema = @Schema(implementation = BaseBookingResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation failure"
    )
    @ApiResponse(
            responseCode = "404",
            description = "User or Flight not found"
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingRequestDTO requestDTO
    ) {
        log.info("REST: Create new booking request received for User ID: {} and Flight ID: {}",
                requestDTO.getUserId(),
                requestDTO.getFlightId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestApiResponse.success(bookingService.createBooking(requestDTO))
        );
    }


    @Operation(
            summary = "Get booking by ID",
            description = "Retrieves a booking by its unique ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Booking retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseBookingResponseDTO.class))
    )
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @SecurityService.isOwnerOfTheBooking(#id, authentication)")
    public ResponseEntity<RestApiResponse<BookingResponseDTO>> getBookingById(
            @Parameter(description = "The unique ID of the booking")
            @PathVariable Long id
    ) {
        log.info("REST: Fetch booking by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(bookingService.getBookingById(id)));
    }

    @Operation(
            summary = "Get booking by PNR code",
            description = "Retrieves a booking by its unique PNR code."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Booking retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseBookingResponseDTO.class))
    )
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @GetMapping("/search/by-pnr")
    @PreAuthorize("hasAuthority('booking:read')")
    public ResponseEntity<RestApiResponse<BookingResponseDTO>> getBookingByPnrCode(
            @Parameter(description = "The unique PNR (Passenger Name Record) code")
            @RequestParam String pnrCode
    ) {
        log.info("REST: Fetch booking by PNR code: {}", pnrCode);
        return ResponseEntity.ok(RestApiResponse.success(bookingService.getBookingByPnrCode(pnrCode)));
    }

    @Operation(
            summary = "Get booking for the authenticated user",
            description = "Retrieves the primary booking associated with the currently authenticated user."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Booking retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseBookingResponseDTO.class))
    )
    @ApiResponse(responseCode = "404", description = "Booking not found for this user")
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<BookingResponseDTO>> getBookingForCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        log.info("REST: Fetch booking for authenticated user: {}", user.getUsername());
        return ResponseEntity.ok(RestApiResponse.success(bookingService.getBookingByUser(user)));
    }


    @Operation(
            summary = "Get all bookings (Paginated)",
            description = "Retrieves a paginated list of all bookings in the system. Requires 'booking:read_all' authority."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of all bookings retrieved",
            content = @Content(schema = @Schema(implementation = PaginationBookingResponseDTO.class))
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<PageResponseDTO<BookingResponseDTO>>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("REST: Fetching all bookings.");
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return ResponseEntity.ok(RestApiResponse.success(bookingService.getAllBookings(pageable)));
    }

    @Operation(
            summary = "Get bookings by status",
            description = "Retrieves a paginated list of bookings matching a specific status (e.g., CONFIRMED, CANCELLED)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of bookings retrieved",
            content = @Content(schema = @Schema(implementation = PaginationBookingResponseDTO.class))
    )
    @GetMapping("/search/by-status")
    @PreAuthorize("hasAuthority('booking:read_all')")
    public ResponseEntity<
            RestApiResponse<PageResponseDTO<BookingResponseDTO>>> getBookingsByStatus(
            @Parameter(description = "The status of the booking (e.g., CONFIRMED, PENDING)")
            @RequestParam BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("REST: Fetching bookings with status: {}", status);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return ResponseEntity.ok(RestApiResponse.success(bookingService.getBookingsByStatus(status, pageable)));
    }

    private static class PaginationBookingResponseDTO
            extends RestApiResponse<PageResponseDTO<BookingResponseDTO>> {
    }

    private static class BaseBookingResponseDTO
            extends RestApiResponse<BookingResponseDTO> {
    }
}
