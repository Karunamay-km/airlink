package com.karunamay.airlink.controller.flight;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.flight.SeatRequestDTO;
import com.karunamay.airlink.dto.flight.SeatResponseDTO;
import com.karunamay.airlink.service.booking.SeatServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/seats")
@Tag(name = "Seat", description = "Endpoints for public seat availability checks and admin seat configuration.")
public class SeatController {

    private final SeatServiceImpl seatService; // Using implementation for direct access to all methods

    @Operation(summary = "Get seats by flight number",
            description = "Retrieves all seats (available/unavailable) for a specific flight.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of seats retrieved",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SeatResponseDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Flight not found")
            })
    @GetMapping("/flight/{flightNo}")
    public ResponseEntity<RestApiResponse<List<SeatResponseDTO>>> getAllSeatsByFlightNo(
            @Parameter(description = "The unique flight number (e.g., 'AI-101')") @PathVariable String flightNo) {
        log.info("REST: Fetching seats for flight {}", flightNo);
        return ResponseEntity.ok(RestApiResponse.success(seatService.getAllByFlightNo(flightNo)));
    }

    @Operation(summary = "Get seat by seat number",
            description = "Retrieves a specific seat by its unique seat number across all flights.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Seat retrieved successfully",
                            content = @Content(schema = @Schema(implementation = SeatResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Seat not found")
            })
    @GetMapping("/search/by-seatNo")
    public ResponseEntity<RestApiResponse<SeatResponseDTO>> getSeatByNo(
            @Parameter(description = "The unique seat number (e.g., 'A1')") @RequestParam String seatNo) {
        log.info("REST: Fetching seat by seat number: {}", seatNo);
        return ResponseEntity.ok(RestApiResponse.success(seatService.getSeatByNo(seatNo)));
    }

    @Operation(summary = "Get all available seats",
            description = "Retrieves a list of all seats currently marked as available across all flights.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of available seats retrieved",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SeatResponseDTO.class))))
            })
    @GetMapping("/available")
    public ResponseEntity<RestApiResponse<List<SeatResponseDTO>>> getSeatByAvailableTrue() {
        log.info("REST: Fetching all available seats.");
        return ResponseEntity.ok(RestApiResponse.success(seatService.getSeatByAvailableTrue()));
    }

    @Operation(summary = "Get available seat count for a flight",
            description = "Retrieves the count of seats marked as available for a specific flight by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Available seat count returned successfully")
            })
    @GetMapping("/count/available/{flightId}")
    public ResponseEntity<RestApiResponse<Integer>> getAvailableSeatCountByFlight(
            @Parameter(description = "The unique ID of the flight") @PathVariable Long flightId) {
        log.info("REST: Fetching available seat count for flight ID: {}", flightId);

        Integer availableCount = seatService.getAvailableSeatCountByFlight(flightId);

        return ResponseEntity.ok(RestApiResponse.success(availableCount));
    }

    @Operation(summary = "Create a new seat",
            description = "Registers a new seat on a flight. **Requires ADMIN role.**",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Seat created successfully",
                            content = @Content(schema = @Schema(implementation = SeatResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden (Requires ADMIN role)"),
                    @ApiResponse(responseCode = "409", description = "Duplicate seat number on the same flight"),
                    @ApiResponse(responseCode = "404", description = "Flight not found")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RestApiResponse<SeatResponseDTO>> createSeat(@Valid @RequestBody SeatRequestDTO requestDTO) {
        log.info("REST: Create new seat request (Admin).");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestApiResponse.success(seatService.createSeat(requestDTO)));
    }

    @Operation(summary = "Update an existing seat",
            description = "Updates details for an existing seat by ID. **Requires ADMIN role.**",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Seat updated successfully",
                            content = @Content(schema = @Schema(implementation = SeatResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden (Requires ADMIN role)"),
                    @ApiResponse(responseCode = "404", description = "Seat or Flight not found"),
                    @ApiResponse(responseCode = "409", description = "New seat number conflicts with an existing seat")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<SeatResponseDTO>> updateSeat(
            @Parameter(description = "The ID of the seat to update") @PathVariable Long id,
            @Valid @RequestBody SeatRequestDTO requestDTO) {
        log.info("REST: Update request for seat id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(seatService.updateSeat(id, requestDTO)));
    }

    @Operation(summary = "Get seat by ID",
            description = "Retrieves a seat by its unique ID. **Requires ADMIN role.**",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Seat retrieved successfully",
                            content = @Content(schema = @Schema(implementation = SeatResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (Requires ADMIN role)"),
                    @ApiResponse(responseCode = "404", description = "Seat not found")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<SeatResponseDTO>> getSeatById(
            @Parameter(description = "The unique ID of the seat") @PathVariable Long id) {
        log.info("REST: Fetch seat by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(seatService.getSeatById(id)));
    }


    @Operation(summary = "Delete a seat",
            description = "Permanently deletes a seat by ID. **Requires ADMIN role.**",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Seat deleted successfully (No Content)"),
                    @ApiResponse(responseCode = "403", description = "Forbidden (Requires ADMIN role)"),
                    @ApiResponse(responseCode = "404", description = "Seat not found"),
                    @ApiResponse(responseCode = "409", description = "Cannot delete: Seat has associated bookings")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteSeat(
            @Parameter(description = "The ID of the seat to delete") @PathVariable Long id) {
        log.info("REST: Delete request for seat id {}", id);
        seatService.deleteSeat(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}