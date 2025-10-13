package com.karunamay.airlink.controller.flight;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.flight.AircraftRequestDTO;
import com.karunamay.airlink.dto.flight.AircraftResponseDTO;
import com.karunamay.airlink.service.flight.AircraftService;
import com.karunamay.airlink.validation.group.OnCreate;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/aircraft")
@Tag(name = "Aircraft Management (Admin)", description = "Operations related to managing aircraft entities (CRUD and lookup).")
public class AircraftController {

    private final AircraftService aircraftService;

    @Operation(summary = "Create a new aircraft",
            description = "Registers a new aircraft with a unique registration number, model, and assigned airline.")
    @ApiResponse(responseCode = "201", description = "Aircraft created successfully", content = @Content(schema = @Schema(implementation = AircraftResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data or validation failure")
    @ApiResponse(responseCode = "404", description = "Airline not found")
    @ApiResponse(responseCode = "409", description = "Registration number already exists")
    @PostMapping
    public ResponseEntity<RestApiResponse<AircraftResponseDTO>> createAircraft(
            @Validated(OnCreate.class) @RequestBody AircraftRequestDTO requestDTO) {
        log.info("REST: Create new aircraft request received (RegNo: {}).", requestDTO.getRegistrationNumber());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestApiResponse.success(aircraftService.createAircraft(requestDTO)));
    }

    @Operation(summary = "Get all aircrafts", description = "Retrieves a list of all aircrafts (both active and inactive).")
    @ApiResponse(responseCode = "200", description = "List of all aircrafts retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AircraftResponseDTO.class))))
    @GetMapping
    public ResponseEntity<RestApiResponse<List<AircraftResponseDTO>>> getAllAircrafts() {
        log.info("REST: Fetching all aircrafts.");
        return ResponseEntity.ok(RestApiResponse.success(aircraftService.getAllAircrafts()));
    }

    @Operation(summary = "Get all active aircrafts", description = "Retrieves a list of all currently active aircrafts.")
    @ApiResponse(responseCode = "200", description = "List of active aircrafts retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AircraftResponseDTO.class))))
    @GetMapping("/active")
    public ResponseEntity<RestApiResponse<List<AircraftResponseDTO>>> getActiveAircrafts() {
        log.info("REST: Fetching all active aircrafts.");
        return ResponseEntity.ok(RestApiResponse.success(aircraftService.getActiveAircrafts()));
    }

    @Operation(summary = "Get aircraft by ID", description = "Retrieves an aircraft by its unique ID.")
    @ApiResponse(responseCode = "200", description = "Aircraft retrieved successfully",
            content = @Content(schema = @Schema(implementation = AircraftResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Aircraft not found")
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<AircraftResponseDTO>> getAircraftById(
            @Parameter(description = "The unique ID of the aircraft") @PathVariable Long id) {
        log.info("REST: Fetch aircraft by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(aircraftService.getAircraftById(id)));
    }

    @Operation(summary = "Search aircraft by registration number",
            description = "Retrieves an aircraft by its unique registration number (case-sensitive).")
    @ApiResponse(responseCode = "200", description = "Aircraft retrieved successfully",
            content = @Content(schema = @Schema(implementation = AircraftResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Aircraft not found")
    @GetMapping("/search/by-registration")
    public ResponseEntity<RestApiResponse<AircraftResponseDTO>> getAircraftByRegistrationNumber(
            @Parameter(description = "The aircraft's unique registration number (e.g., 'VT-A320')")
            @RequestParam String registrationNumber) {
        log.info("REST: Fetch aircraft by registration number: {}", registrationNumber);
        return ResponseEntity.ok(RestApiResponse.success(aircraftService.getAircraftByRegistrationNumber(registrationNumber)));
    }

    @Operation(summary = "Search aircraft by model", description = "Retrieves an aircraft by its model name (case-sensitive).")
    @ApiResponse(responseCode = "200", description = "Aircraft retrieved successfully",
            content = @Content(schema = @Schema(implementation = AircraftResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Aircraft not found")
    @GetMapping("/search/by-model")
    public ResponseEntity<RestApiResponse<AircraftResponseDTO>> getAircraftByModel(
            @Parameter(description = "The aircraft's model name (e.g., 'Airbus A320')") @RequestParam String model) {
        log.info("REST: Fetch aircraft by model: {}", model);
        return ResponseEntity.ok(RestApiResponse.success(aircraftService.getAircraftByModel(model)));
    }

    @Operation(summary = "Search aircrafts by airline name",
            description = "Retrieves a list of aircrafts belonging to a specific airline.")
    @ApiResponse(responseCode = "200", description = "List of aircrafts retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AircraftResponseDTO.class))))
    @GetMapping("/search/by-airline")
    public ResponseEntity<RestApiResponse<List<AircraftResponseDTO>>> getAircraftsByAirlineName(
            @Parameter(description = "The full name of the airline") @RequestParam String airlineName) {
        log.info("REST: Fetching aircrafts by airline: {}", airlineName);
        return ResponseEntity.ok(RestApiResponse.success(aircraftService.getAircraftsByAirlineName(airlineName)));
    }

    @Operation(summary = "Update an existing aircraft", description = "Updates details for an existing aircraft by ID.")
    @ApiResponse(responseCode = "200", description = "Aircraft updated successfully",
            content = @Content(schema = @Schema(implementation = AircraftResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Aircraft or new Airline not found")
    @ApiResponse(responseCode = "409", description = "Updated registration number already exists")
    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<AircraftResponseDTO>> updateAircraft(
            @Parameter(description = "The ID of the aircraft to update") @PathVariable Long id,
            @Valid @RequestBody AircraftRequestDTO requestDTO) {
        log.info("REST: Update request for aircraft id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(aircraftService.updateAircraft(id, requestDTO)));
    }

    @Operation(summary = "Delete an aircraft", description = "Permanently deletes an aircraft by ID.")
    @ApiResponse(responseCode = "204", description = "Aircraft deleted successfully (No Content)")
    @ApiResponse(responseCode = "404", description = "Aircraft not found")
    @ApiResponse(responseCode = "409", description = "Cannot delete: Aircraft has associated flights")
    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteAircraft(
            @Parameter(description = "The ID of the aircraft to delete") @PathVariable Long id) {
        log.info("REST: Delete request for aircraft id {}", id);
        aircraftService.deleteAircraft(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
