package com.karunamay.airlink.controller.flight;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.flight.AirportRequestDTO;
import com.karunamay.airlink.dto.flight.AirportResponseDTO;
import com.karunamay.airlink.service.flight.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/airport")
@Tag(name = "Airport Management (Admin)", description = "Operations related to managing airport entities (CRUD and lookup).")
public class AirportController {

    private final AirportService airportService;

    @Operation(summary = "Create a new airport", description = "Registers a new airport with a unique code and name.")
    @ApiResponse(responseCode = "201", description = "Airport created successfully", content = @Content(schema = @Schema(implementation = AirportResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data or validation failure")
    @ApiResponse(responseCode = "409", description = "Airport code or name already exists")
    @PostMapping
    public ResponseEntity<RestApiResponse<AirportResponseDTO>> createAirport(
            @Valid @RequestBody AirportRequestDTO requestDTO) {
        log.info("REST: Create new airport request received (Code: {}).", requestDTO.getCode());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestApiResponse.success(airportService.createAirport(requestDTO)));
    }

    @Operation(summary = "Get all airports", description = "Retrieves a list of all airports (both active and inactive).")
    @ApiResponse(responseCode = "200", description = "List of all airports retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AirportResponseDTO.class))))
    @GetMapping
    public ResponseEntity<RestApiResponse<List<AirportResponseDTO>>> getAllAirports() {
        log.info("REST: Fetching all airports.");
        return ResponseEntity.ok(RestApiResponse.success(airportService.getAllAirports()));
    }

    @Operation(summary = "Get all active airports", description = "Retrieves a list of all currently active airports.")
    @ApiResponse(responseCode = "200", description = "List of active airports retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AirportResponseDTO.class))))
    @GetMapping("/active")
    public ResponseEntity<RestApiResponse<List<AirportResponseDTO>>> getActiveAirports() {
        log.info("REST: Fetching all active airports.");
        return ResponseEntity.ok(RestApiResponse.success(airportService.getActiveAirports()));
    }

    @Operation(summary = "Get airport by ID", description = "Retrieves an airport by its unique ID.")
    @ApiResponse(responseCode = "200", description = "Airport retrieved successfully",
            content = @Content(schema = @Schema(implementation = AirportResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Airport not found")
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<AirportResponseDTO>> getAirportById(
            @Parameter(description = "The unique ID of the airport") @PathVariable Long id) {
        log.info("REST: Fetch airport by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(airportService.getAirportById(id)));
    }

    @Operation(summary = "Search airport by IATA/ICAO code",
            description = "Retrieves an airport by its unique code (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "Airport retrieved successfully",
            content = @Content(schema = @Schema(implementation = AirportResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Airport not found")
    @GetMapping("/search/by-code")
    public ResponseEntity<RestApiResponse<AirportResponseDTO>> getAirportByCode(
            @Parameter(description = "The IATA or ICAO code of the airport (e.g., 'DEL', 'VIDP')") @RequestParam String code) {
        log.info("REST: Fetch airport by code: {}", code);
        return ResponseEntity.ok(RestApiResponse.success(airportService.getAirportByCode(code)));
    }

    @Operation(summary = "Search airport by full name", description = "Retrieves an airport by its exact name (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "Airport retrieved successfully",
            content = @Content(schema = @Schema(implementation = AirportResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Airport not found")
    @GetMapping("/search/by-name")
    public ResponseEntity<RestApiResponse<AirportResponseDTO>> getAirportByName(
            @Parameter(description = "The full name of the airport") @RequestParam String name) {
        log.info("REST: Fetch airport by name: {}", name);
        return ResponseEntity.ok(RestApiResponse.success(airportService.getAirportByName(name)));
    }

    @Operation(summary = "Search airports by city", description = "Retrieves a list of airports serving a specific city (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "List of airports retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AirportResponseDTO.class))))
    @GetMapping("/search/by-city")
    public ResponseEntity<RestApiResponse<List<AirportResponseDTO>>> getAirportsByCity(
            @Parameter(description = "The city the airport is located in") @RequestParam String city) {
        log.info("REST: Fetching airports by city: {}", city);
        return ResponseEntity.ok(RestApiResponse.success(airportService.getAirportsByCity(city)));
    }

    @Operation(summary = "Update an existing airport", description = "Updates details for an existing airport by ID.")
    @ApiResponse(responseCode = "200", description = "Airport updated successfully",
            content = @Content(schema = @Schema(implementation = AirportResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Airport not found")
    @ApiResponse(responseCode = "409", description = "Updated code or name already exists on another airport")
    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<AirportResponseDTO>> updateAirport(
            @Parameter(description = "The ID of the airport to update") @PathVariable Long id,
            @Valid @RequestBody AirportRequestDTO requestDTO) {
        log.info("REST: Update request for airport id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(airportService.updateAirport(id, requestDTO)));
    }


    @Operation(summary = "Delete an airport", description = "Permanently deletes an airport by ID.")
    @ApiResponse(responseCode = "204", description = "Airport deleted successfully (No Content)")
    @ApiResponse(responseCode = "404", description = "Airport not found")
    @ApiResponse(responseCode = "409", description = "Cannot delete: Airport has associated flights")
    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteAirport(
            @Parameter(description = "The ID of the airport to delete") @PathVariable Long id) {
        log.info("REST: Delete request for airport id {}", id);
        airportService.deleteAirport(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}