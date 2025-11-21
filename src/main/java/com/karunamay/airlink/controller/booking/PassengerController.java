package com.karunamay.airlink.controller.booking;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.booking.PassengerRequestDTO;
import com.karunamay.airlink.dto.booking.PassengerResponseDTO;
import com.karunamay.airlink.dto.error.ErrorResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.service.booking.PassengerService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/passenger")
@Tag(
        name = "Passenger",
        description = "Operations related to managing passenger entities (CRUD and lookup)."
)
public class PassengerController {

    private final PassengerService passengerService;

    @Operation(
            summary = "Create a new passenger",
            description = "Registers a new passenger and assigns a seat."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Passenger created and seat assigned successfully",
            content = @Content(schema = @Schema(implementation = BasePassengerResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation failure",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "The requested seat is already occupied",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @PostMapping
    @PreAuthorize("hasAuthority('passenger:create')")
    public ResponseEntity<RestApiResponse<PassengerResponseDTO>> createPassenger(
            @Valid @RequestBody PassengerRequestDTO requestDTO
    ) {
        log.info("REST: Create new passenger (First Name: {}, Last Name: {}).",
                requestDTO.getFirstName(), requestDTO.getLastName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestApiResponse.success(passengerService.createPassenger(requestDTO)));
    }

    @Operation(
            summary = "Get passenger by ID",
            description = "Retrieves a passenger by their unique ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Passenger retrieved successfully",
            content = @Content(schema = @Schema(implementation = BasePassengerResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Passenger not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<PassengerResponseDTO>> getPassengerById(
            @Parameter(description = "Passenger ID") @PathVariable Long id
    ) {
        log.info("REST: Fetch passenger by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(passengerService.getPassengerById(id)));
    }

    @Operation(
            summary = "Get all passengers",
            description = "Retrieves a paginated list of all passenger records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of all passengers retrieved",
            content = @Content(schema = @Schema(implementation = PaginationPassengerResponseDTO.class))
    )
    @GetMapping
    public ResponseEntity<RestApiResponse<PageResponseDTO<PassengerResponseDTO>>> getAllPassengers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info("REST: Fetching all passengers (page: {}, size: {}).", page, size);

        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return ResponseEntity.ok(
                RestApiResponse.success(passengerService.getAllPassengers(pageable))
        );
    }

    @Operation(
            summary = "Update an existing passenger",
            description = "Updates details for an existing passenger by ID, including seat reassignment."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Passenger updated successfully",
            content = @Content(schema = @Schema(implementation = BasePassengerResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Passenger or new Seat not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Requested new seat is already occupied",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('passenger:update')")
    public ResponseEntity<RestApiResponse<PassengerResponseDTO>> updatePassenger(
            @Parameter(description = "Passenger ID") @PathVariable Long id,
            @Valid @RequestBody PassengerRequestDTO requestDTO
    ) {
        log.info("REST: Update passenger id {} ({} {}).",
                id, requestDTO.getFirstName(), requestDTO.getLastName());

        return ResponseEntity.ok(
                RestApiResponse.success(passengerService.updatePassengerById(id, requestDTO))
        );
    }

    @Operation(
            summary = "Delete a passenger",
            description = "Permanently deletes a passenger record by ID."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Passenger deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Passenger not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('passenger:delete')")
    public ResponseEntity<RestApiResponse<Void>> deletePassenger(
            @Parameter(description = "Passenger ID") @PathVariable Long id
    ) {
        log.info("REST: Delete passenger id {}", id);
        passengerService.deletePassenger(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private static class PaginationPassengerResponseDTO
            extends RestApiResponse<PageResponseDTO<PassengerResponseDTO>> {
    }

    private static class BasePassengerResponseDTO
            extends RestApiResponse<PassengerResponseDTO> {
    }
}
