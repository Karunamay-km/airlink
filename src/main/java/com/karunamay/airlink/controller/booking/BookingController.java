package com.karunamay.airlink.controller.booking;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.booking.BookingRequestDTO;
import com.karunamay.airlink.dto.booking.BookingResponseDTO;
import com.karunamay.airlink.dto.error.ErrorResponseDTO;
import com.karunamay.airlink.service.booking.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/booking")
@Tag(name = "Booking", description = "Operations for creating, retrieving, and managing flight bookings.")
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "Get booking details by ID",
            description = "Retrieves a single booking record. Accessible by ADMIN or the owner of the booking.",
            tags = {"Booking Management"},
            security = @SecurityRequirement(name = "bearerAuth") // Assumes Bearer Token authentication
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Booking retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingResponseDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User is not the owner or an ADMIN",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Booking does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @SecurityService.isOwnerOfTheBooking(#id, authentication)")
    public ResponseEntity<RestApiResponse<BookingResponseDTO>> getBookingById(@PathVariable Long id) {
        log.info("REST: Booking fetch request for id {}", id);
        return ResponseEntity.ok(
                RestApiResponse.success(
                        bookingService.getBookingById(id)
                )
        );
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('booking:create')")
    public ResponseEntity<RestApiResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingRequestDTO requestDTO
    ) {
        log.info("REST: New booking create request");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestApiResponse.success(bookingService.createBooking(requestDTO)));
    }
}
