package com.karunamay.airlink.controller.flight;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.flight.FlightRequestDTO;
import com.karunamay.airlink.dto.flight.FlightResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.service.flight.FlightService;
import com.karunamay.airlink.validation.group.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/flights")
@Tag(
    name = "Flight",
    description = "Operations related to managing flight entities (CRUD and lookup)."
)
public class FlightController {

    private final FlightService flightService;

    @Operation(
        summary = "Create a new flight",
        description = "Registers a new flight, linking it to an airline, aircraft, and airports."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Flight created successfully",
        content = @Content(
            schema = @Schema(implementation = BaseFlightResponseDTO.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or validation failure"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Airline, Aircraft, or Airport not found"
    )
    @ApiResponse(
        responseCode = "409",
        description = "Flight number already exists or source/destination airports are the same"
    )
    @PostMapping
    @PreAuthorize("hasAuthority('flight:create')")
    public ResponseEntity<RestApiResponse<FlightResponseDTO>> createFlight(
        @Validated(OnCreate.class) @RequestBody FlightRequestDTO requestDTO
    ) {
        log.info(
            "REST: Create new flight request received (FlightNo: {}).",
            requestDTO.getFlightNo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
            RestApiResponse.success(flightService.createFlight(requestDTO))
        );
    }

    @Operation(
        summary = "Get all flights",
        description = "Retrieves a list of all flights in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of all flights retrieved",
        content = @Content(
            schema = @Schema(implementation = PaginationFlightResponseDTO.class)
        )
    )
    @GetMapping
    public ResponseEntity<
        RestApiResponse<PageResponseDTO<FlightResponseDTO>>
    > getAllFlights(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info("REST: Fetching all flights.");
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return ResponseEntity.ok(
            RestApiResponse.success(flightService.getAllFlights(pageable))
        );
    }

    @Operation(
        summary = "Get flight by ID",
        description = "Retrieves a flight by its unique ID."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Flight retrieved successfully",
        content = @Content(
            schema = @Schema(implementation = BaseFlightResponseDTO.class)
        )
    )
    @ApiResponse(responseCode = "404", description = "Flight not found")
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<FlightResponseDTO>> getFlightById(
        @Parameter(
            description = "The unique ID of the flight"
        ) @PathVariable Long id
    ) {
        log.info("REST: Fetch flight by id {}", id);
        return ResponseEntity.ok(
            RestApiResponse.success(flightService.getFlightById(id))
        );
    }

    @Operation(
        summary = "Search flight by flight number",
        description = "Retrieves a flight by its unique flight number (e.g., 'AI-101')."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Flight retrieved successfully",
        content = @Content(
            schema = @Schema(implementation = PaginationFlightResponseDTO.class)
        )
    )
    @ApiResponse(responseCode = "404", description = "Flight not found")
    @GetMapping("/search/by-flightNo")
    public ResponseEntity<
        RestApiResponse<FlightResponseDTO>
    > getFlightByFlightNo(
        @Parameter(
            description = "The unique flight number"
        ) @RequestParam String flightNo
    ) {
        log.info("REST: Fetch flight by flight number: {}", flightNo);
        return ResponseEntity.ok(
            RestApiResponse.success(flightService.getFlightByFlightNo(flightNo))
        );
    }

    @Operation(
        summary = "Search flights by departure airport name",
        description = "Retrieves a list of flights departing from the specified airport name."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of flights retrieved",
        content = @Content(
            schema = @Schema(implementation = PaginationFlightResponseDTO.class)
        )
    )
    @GetMapping("/search/by-departure-airport")
    public ResponseEntity<
        RestApiResponse<PageResponseDTO<FlightResponseDTO>>
    > getFlightsByDepartureAirportName(
        @Parameter(
            description = "The full name of the departure airport"
        ) @RequestParam String airportName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info(
            "REST: Fetching flights departing from airport: {}",
            airportName
        );
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return ResponseEntity.ok(
            RestApiResponse.success(
                flightService.getFlightsByDepartureAirportName(
                    airportName,
                    pageable
                )
            )
        );
    }

    @Operation(
        summary = "Search flights by arrival airport name",
        description = "Retrieves a list of flights arriving at the specified airport name."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of flights retrieved",
        content = @Content(
            schema = @Schema(implementation = PaginationFlightResponseDTO.class)
        )
    )
    @GetMapping("/search/by-arrival-airport")
    public ResponseEntity<
        RestApiResponse<PageResponseDTO<FlightResponseDTO>>
    > getFlightsByArrivalAirportName(
        @Parameter(
            description = "The full name of the arrival airport"
        ) @RequestParam String airportName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info("REST: Fetching flights arriving at airport: {}", airportName);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return ResponseEntity.ok(
            RestApiResponse.success(
                flightService.getFlightsByArrivalAirportName(
                    airportName,
                    pageable
                )
            )
        );
    }

    @Operation(
        summary = "Search for available flights",
        description = "Searches for flights based on travel route, minimum departure time, and required seat count.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of matching flights retrieved",
                content = @Content(
                    schema = @Schema(
                        implementation = PaginationFlightResponseDTO.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input (e.g., date format or required parameters missing)"
            ),
        }
    )
    @GetMapping("/search")
    public ResponseEntity<
        RestApiResponse<PageResponseDTO<FlightResponseDTO>>
    > searchFlights(
        @Parameter(description = "Source airport Id (e.g., 1)") @RequestParam(
            "src"
        ) Long src,
        @Parameter(description = "Destination airport Id") @RequestParam(
            "dest"
        ) Long dest,
        @Parameter(
            description = "Departure date and time (ISO format: YYYY-MM-DDTHH:MM:SS)"
        ) @RequestParam("departureTime") @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
        ) LocalDateTime departureTime,
        @Parameter(
            description = "Minimum number of seats required"
        ) @RequestParam("seat") Integer seat,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info(
            "REST: Search flights request (Src: {}, Dest: {}, Time: {})",
            src,
            dest,
            departureTime
        );
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        PageResponseDTO<FlightResponseDTO> flights =
            flightService.getFlightsBySearchParameters(
                src,
                dest,
                departureTime,
                seat,
                pageable
            );
        return ResponseEntity.ok(RestApiResponse.success(flights));
    }

    @Operation(
        summary = "Update an existing flight",
        description = "Updates details for an existing flight by ID. Allows partial updates."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Flight updated successfully",
        content = @Content(
            schema = @Schema(implementation = BaseFlightResponseDTO.class)
        )
    )
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(
        responseCode = "404",
        description = "Flight, Airline, Aircraft, or Airport not found"
    )
    @ApiResponse(
        responseCode = "409",
        description = "Updated flight number already exists or source/destination airports are the same"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('flight:update')")
    public ResponseEntity<RestApiResponse<FlightResponseDTO>> updateFlight(
        @Parameter(
            description = "The ID of the flight to update"
        ) @PathVariable Long id,
        @Valid @RequestBody FlightRequestDTO requestDTO
    ) {
        log.info("REST: Update request for flight id {}", id);
        return ResponseEntity.ok(
            RestApiResponse.success(flightService.updateFlight(id, requestDTO))
        );
    }

    @Operation(
        summary = "Delete a flight",
        description = "Permanently deletes a flight by ID."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Flight deleted successfully (No Content)"
    )
    @ApiResponse(responseCode = "404", description = "Flight not found")
    @ApiResponse(
        responseCode = "409",
        description = "Cannot delete: Flight has associated bookings"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('flight:delete')")
    public ResponseEntity<RestApiResponse<Void>> deleteFlight(
        @Parameter(
            description = "The ID of the flight to delete"
        ) @PathVariable Long id
    ) {
        log.info("REST: Delete request for flight id {}", id);
        flightService.deleteFlight(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private static class PaginationFlightResponseDTO
        extends RestApiResponse<PageResponseDTO<FlightResponseDTO>> {}

    private static class BaseFlightResponseDTO
        extends RestApiResponse<FlightResponseDTO> {}
}
