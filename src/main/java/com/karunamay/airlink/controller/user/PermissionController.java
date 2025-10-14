package com.karunamay.airlink.controller.user;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.error.ErrorResponseDTO;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionRequestDTO;
import com.karunamay.airlink.dto.permission.PermissionResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionUpdateRequestDTO;
import com.karunamay.airlink.service.permission.PermissionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/permission")
@Tag(name = "Permission", description = "CRUD operations for managing application permissions. Requires admin/elevated privileges.")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionServiceImpl permissionService;

    @Operation(
            summary = "Create a new permission",
            description = "Defines a new permission (e.g., 'user:create'). Requires a unique name.",
            tags = {"Permission Management (Admin)"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Permission created successfully",
                    content = @Content(schema = @Schema(implementation = PermissionResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Permission name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<RestApiResponse<PermissionResponseDTO>> createPermission(
            @Valid @RequestBody PermissionRequestDTO requestDTO) {
        log.info("REST: Create new permission request received.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestApiResponse.success(permissionService.createPermission(requestDTO)));
    }


    @Operation(
            summary = "Get permission by ID",
            description = "Retrieves a single permission definition.",
            tags = {"Permission Management (Admin)"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Permission retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PermissionResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Permission does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<PermissionResponseDTO>> getPermissionById(@PathVariable Long id) {
        log.info("REST: Fetch permission by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(permissionService.getPermissionById(id)));
    }


    @Operation(
            summary = "Get all permissions with pagination",
            description = "Retrieves a paginated list of all defined permissions.",
            tags = {"Permission Management (Admin)"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of permissions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResponseDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<RestApiResponse<PageResponseDTO<PermissionResponseDTO>>> getAllPermissions(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.info("REST: Fetch all permissions with pagination: {}", pageable);
        return ResponseEntity.ok(RestApiResponse.success(permissionService.getAllPermissions(pageable)));
    }


    @Operation(summary = "Find permissions by resource name", tags = {"Permission Management (Admin)"})
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionResponseDTO.class)))
            )
    )
    @GetMapping("/search/by-resource")
    public ResponseEntity<RestApiResponse<List<PermissionResponseDTO>>> getPermissionsByResource(
            @RequestParam String resource) {
        log.info("REST: Fetch permissions by resource: {}", resource);
        return ResponseEntity.ok(RestApiResponse.success(permissionService.getPermissionsByResource(resource)));
    }


    @Operation(summary = "Find permissions by action name", tags = {"Permission Management (Admin)"})
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionResponseDTO.class)))
            )
    )
    @GetMapping("/search/by-action")
    public ResponseEntity<RestApiResponse<List<PermissionResponseDTO>>> getPermissionsByAction(
            @RequestParam String action) {
        log.info("REST: Fetch permissions by action: {}", action);
        return ResponseEntity.ok(RestApiResponse.success(permissionService.getPermissionsByAction(action)));
    }


    @Operation(summary = "Find permissions by full name", tags = {"Permission Management (Admin)"})
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionResponseDTO.class)))
            )
    )
    @GetMapping("/search/by-name")
    public ResponseEntity<RestApiResponse<List<PermissionResponseDTO>>> getPermissionsByName(
            @RequestParam String name) {
        log.info("REST: Fetch permissions by name: {}", name);
        return ResponseEntity.ok(RestApiResponse.success(permissionService.getPermissionsByName(name)));
    }

    @Operation(summary = "Get all distinct resource names", description = "Returns a list of all unique resource identifiers (e.g., ['user', 'booking', 'flight']).", tags = {"Permission Management (Admin)"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "List of resources retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))
            )
    })
    @GetMapping("/resources")
    public ResponseEntity<RestApiResponse<List<String>>> getAllPermissionResource() {
        log.info("REST: Fetch all distinct permission resources");
        return ResponseEntity.ok(RestApiResponse.success(permissionService.getAllPermissionResource()));
    }

    @Operation(summary = "Get all distinct action names", description = "Returns a list of all unique action identifiers (e.g., ['create', 'read', 'delete']).", tags = {"Permission Management (Admin)"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "List of actions retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))
            )
    })
    @GetMapping("/actions")
    public ResponseEntity<RestApiResponse<List<String>>> getAllPermissionAction() {
        log.info("REST: Fetch all distinct permission actions");
        return ResponseEntity.ok(RestApiResponse.success(permissionService.getAllPermissionAction()));
    }

    @Operation(
            summary = "Update an existing permission",
            description = "Updates the resource and/or action parts of an existing permission. The full name is derived from these parts.",
            tags = {"Permission Management (Admin)"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Permission updated successfully",
                    content = @Content(schema = @Schema(implementation = PermissionResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Permission to update does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Updated permission name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<PermissionResponseDTO>> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionUpdateRequestDTO requestDTO) {
        log.info("REST: Update request for permission id {}", id);
        PermissionResponseDTO response = permissionService.updatePermission(id, requestDTO);
        return ResponseEntity.ok(RestApiResponse.success(response));
    }


    @Operation(
            summary = "Delete a permission",
            description = "Permanently deletes a permission by ID. Deletion may fail if the permission is assigned to roles.",
            tags = {"Permission Management (Admin)"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Permission deleted successfully (No Content)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Permission to delete does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Cannot delete permission (e.g., still linked to a role)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deletePermission(@PathVariable Long id) {
        log.info("REST: Delete permission by id {}", id);
        permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
