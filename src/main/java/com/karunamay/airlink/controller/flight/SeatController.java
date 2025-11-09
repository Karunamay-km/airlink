package com.karunamay.airlink.controller.flight;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.flight.SeatRequestDTO;
import com.karunamay.airlink.dto.flight.SeatResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.service.booking.SeatService;
import com.karunamay.airlink.validation.group.OnCreate;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/seats")
@Tag(
    name = "Seat",
    description = "Operations related to managing seat entities (CRUD and search)."
)
public class SeatController {

    private final SeatService seatService;

    private static final int MAX_PAGE_SIZE = 1000;
    private static final int ALL_PAGE_SIZE = -1;

    @Operation(
        summary = "Create a new seat",
        description = "Registers a new seat for a specific flight."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Seat created successfully",
        content = @Content(
            schema = @Schema(implementation = BaseSeatResponseDTO.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or validation failure"
    )
    @ApiResponse(responseCode = "404", description = "Flight not found")
    @ApiResponse(
        responseCode = "409",
        description = "Seat number already exists on this flight"
    )
    @PostMapping
    @PreAuthorize("hasAuthority('seat:create')")
    public ResponseEntity<RestApiResponse<SeatResponseDTO>> createSeat(
        @Validated(OnCreate.class) @RequestBody SeatRequestDTO requestDTO
    ) {
        log.info(
            "REST: Create seat request (SeatNo: {}, FlightId: {})",
            requestDTO.getSeatNo(),
            requestDTO.getFlightId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
            RestApiResponse.success(seatService.createSeat(requestDTO))
        );
    }

    @Operation(
        summary = "Get all available seats",
        description = "Retrieves a paginated list of all available seats across flights."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of available seats retrieved",
        content = @Content(
            schema = @Schema(implementation = PaginationSeatResponseDTO.class)
        )
    )
    @GetMapping("/available")
    public ResponseEntity<
        RestApiResponse<PageResponseDTO<SeatResponseDTO>>
    > getAvailableSeats(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info("REST: Fetching available seats.");
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return ResponseEntity.ok(
            RestApiResponse.success(
                seatService.getSeatByAvailableTrue(pageable)
            )
        );
    }

    @Operation(
        summary = "Get all seats for a flight",
        description = "Retrieves a paginated list of seats for a given flight number. **Use size=-1 to fetch all seats in one page.**"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of seats retrieved successfully",
        content = @Content(
            schema = @Schema(implementation = PaginationSeatResponseDTO.class)
        )
    )
    @ApiResponse(responseCode = "404", description = "Flight not found")
    @GetMapping("/by-flightNo")
    public ResponseEntity<
        RestApiResponse<PageResponseDTO<SeatResponseDTO>>
    > getSeatsByFlightNo(
        @Parameter(
            description = "Flight number (e.g., AI-101)"
        ) @RequestParam String flightNo,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info("REST: Fetching seats for flight number: {}", flightNo);

        int pageSize = size;

        if (size == ALL_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        Pageable pageable = PageRequest.of(page, pageSize, direction, sortBy);
        return ResponseEntity.ok(
            RestApiResponse.success(
                seatService.getAllByFlightNo(flightNo, pageable)
            )
        );
    }

    @Operation(
        summary = "Get seat by ID",
        description = "Retrieves a seat by its unique ID."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Seat retrieved successfully",
        content = @Content(
            schema = @Schema(implementation = BaseSeatResponseDTO.class)
        )
    )
    @ApiResponse(responseCode = "404", description = "Seat not found")
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<SeatResponseDTO>> getSeatById(
        @Parameter(description = "Seat ID") @PathVariable Long id
    ) {
        log.info("REST: Fetch seat by id {}", id);
        return ResponseEntity.ok(
            RestApiResponse.success(seatService.getSeatById(id))
        );
    }

    @Operation(
        summary = "Get seat by seat number",
        description = "Retrieves a seat by its seat number (unique within a flight)."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Seat retrieved successfully",
        content = @Content(
            schema = @Schema(implementation = BaseSeatResponseDTO.class)
        )
    )
    @ApiResponse(responseCode = "404", description = "Seat not found")
    @GetMapping("/by-seatNo")
    public ResponseEntity<RestApiResponse<SeatResponseDTO>> getSeatBySeatNo(
        @Parameter(
            description = "Seat number (e.g., 12A)"
        ) @RequestParam String seatNo
    ) {
        log.info("REST: Fetch seat by seat number {}", seatNo);
        return ResponseEntity.ok(
            RestApiResponse.success(seatService.getSeatByNo(seatNo))
        );
    }

    @Operation(
        summary = "Get available seat count by flight ID",
        description = "Returns the number of available seats for a specific flight."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Seat count retrieved successfully",
        content = @Content(schema = @Schema(implementation = Integer.class))
    )
    @ApiResponse(responseCode = "404", description = "Flight not found")
    @GetMapping("/available/count")
    public ResponseEntity<
        RestApiResponse<Integer>
    > getAvailableSeatCountByFlight(
        @Parameter(description = "Flight ID") @RequestParam Long flightId
    ) {
        log.info("REST: Fetch available seat count for flight id {}", flightId);
        return ResponseEntity.ok(
            RestApiResponse.success(
                seatService.getAvailableSeatCountByFlight(flightId)
            )
        );
    }

    @Operation(
        summary = "Update an existing seat",
        description = "Updates details of a seat by ID."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Seat updated successfully",
        content = @Content(
            schema = @Schema(implementation = BaseSeatResponseDTO.class)
        )
    )
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Seat not found")
    @ApiResponse(responseCode = "409", description = "Seat number conflict")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('seat:update') or hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<SeatResponseDTO>> updateSeat(
        @Parameter(description = "Seat ID") @PathVariable Long id,
        @Valid @RequestBody SeatRequestDTO requestDTO
    ) {
        log.info("REST: Update request for seat id {}", id);
        return ResponseEntity.ok(
            RestApiResponse.success(seatService.updateSeat(id, requestDTO))
        );
    }

    @Operation(
        summary = "Delete a seat",
        description = "Deletes a seat by its ID."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Seat deleted successfully"
    )
    @ApiResponse(responseCode = "404", description = "Seat not found")
    @ApiResponse(
        responseCode = "409",
        description = "Seat has existing bookings and cannot be deleted"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('seat:delete')")
    public ResponseEntity<RestApiResponse<Void>> deleteSeat(
        @Parameter(description = "Seat ID") @PathVariable Long id
    ) {
        log.info("REST: Delete request for seat id {}", id);
        seatService.deleteSeat(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private static class PaginationSeatResponseDTO
        extends RestApiResponse<PageResponseDTO<SeatResponseDTO>> {}

    private static class BaseSeatResponseDTO
        extends RestApiResponse<SeatResponseDTO> {}
}
