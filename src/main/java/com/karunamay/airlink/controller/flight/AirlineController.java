package com.karunamay.airlink.controller.flight;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.flight.AirlineRequestDTO;
import com.karunamay.airlink.dto.flight.AirlineResponseDTO;
import com.karunamay.airlink.service.flight.AirlineService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/airline")
@Tag(name = "Airline Management (Admin)", description = "Operations related to managing airlines (CRUD, lookup, status).")
public class AirlineController {

    private final AirlineService airlineService;

    @Operation(summary = "Create a new airline", description = "Registers a new airline with a unique code and name.")
    @ApiResponse(responseCode = "201", description = "Airline created successfully",
            content = @Content(schema = @Schema(implementation = AirlineResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Airline code or name already exists")
    @PostMapping
    public ResponseEntity<RestApiResponse<AirlineResponseDTO>> createAirline(
            @Validated(OnCreate.class) @RequestBody AirlineRequestDTO requestDTO) {
        log.info("REST: Create new airline request received (Code: {}).", requestDTO.getCode());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestApiResponse.success(airlineService.createAirline(requestDTO)));
    }


    @Operation(summary = "Get all airlines", description = "Retrieves a list of all airlines (both active and inactive).")
    @ApiResponse(responseCode = "200", description = "List of all airlines retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AirlineResponseDTO.class))))
    @GetMapping
    public ResponseEntity<RestApiResponse<List<AirlineResponseDTO>>> getAllAirlines() {
        log.info("REST: Fetching all airlines.");
        return ResponseEntity.ok(RestApiResponse.success(airlineService.getAllAirlines()));
    }

    @Operation(summary = "Get airline by ID", description = "Retrieves an airline by its unique ID.")
    @ApiResponse(responseCode = "200", description = "Airline retrieved successfully",
            content = @Content(schema = @Schema(implementation = AirlineResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Airline not found")
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<AirlineResponseDTO>> getAirlineById(
            @Parameter(description = "The unique ID of the airline") @PathVariable Long id) {
        log.info("REST: Fetch airline by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(airlineService.getAirlineById(id)));
    }

    @Operation(summary = "Search airline by 2-character code", description = "Retrieves an airline by its unique IATA/ICAO code.")
    @ApiResponse(responseCode = "200", description = "Airline retrieved successfully",
            content = @Content(schema = @Schema(implementation = AirlineResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Airline not found")
    @GetMapping("/search/by-code")
    public ResponseEntity<RestApiResponse<AirlineResponseDTO>> getAirlineByCode(
            @Parameter(description = "The 2-character code of the airline (e.g., 'AI')") @RequestParam String code) {
        log.info("REST: Fetch airline by code: {}", code);
        return ResponseEntity.ok(RestApiResponse.success(airlineService.getAirlineByCode(code)));
    }

    @Operation(summary = "Search airline by full name", description = "Retrieves an airline by its exact name.")
    @ApiResponse(responseCode = "200", description = "Airline retrieved successfully",
            content = @Content(schema = @Schema(implementation = AirlineResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Airline not found")
    @GetMapping("/search/by-name")
    public ResponseEntity<RestApiResponse<AirlineResponseDTO>> getAirlineByName(
            @Parameter(description = "The full name of the airline") @RequestParam String name) {
        log.info("REST: Fetch airline by name: {}", name);
        return ResponseEntity.ok(RestApiResponse.success(airlineService.getAirlineByName(name)));
    }

    @Operation(summary = "Get all active airlines", description = "Retrieves a list of all currently active airlines.")
    @ApiResponse(responseCode = "200", description = "List of active airlines retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AirlineResponseDTO.class))))
    @GetMapping("/active")
    public ResponseEntity<RestApiResponse<List<AirlineResponseDTO>>> getActiveAirlines() {
        log.info("REST: Fetching all active airlines.");
        return ResponseEntity.ok(RestApiResponse.success(airlineService.getActiveAirlines()));
    }


    @Operation(summary = "Update an existing airline", description = "Updates details for an existing airline by ID.")
    @ApiResponse(responseCode = "200", description = "Airline updated successfully",
            content = @Content(schema = @Schema(implementation = AirlineResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Airline not found")
    @ApiResponse(responseCode = "409", description = "Updated code or name already exists on another airline")
    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<AirlineResponseDTO>> updateAirline(
            @Parameter(description = "The ID of the airline to update") @PathVariable Long id,
            @Valid @RequestBody AirlineRequestDTO requestDTO) {
        log.info("REST: Update request for airline id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(airlineService.updateAirline(id, requestDTO)));
    }


    @Operation(summary = "Delete an airline", description = "Permanently deletes an airline by ID.")
    @ApiResponse(responseCode = "204", description = "Airline deleted successfully (No Content)")
    @ApiResponse(responseCode = "404", description = "Airline not found")
    @ApiResponse(responseCode = "409", description = "Cannot delete: Airline has associated flights or aircrafts")
    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteAirline(
            @Parameter(description = "The ID of the airline to delete") @PathVariable Long id) {
        log.info("REST: Delete request for airline id {}", id);
        airlineService.deleteAirline(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
