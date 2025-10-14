package com.karunamay.airlink.controller.user;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.error.ErrorResponseDTO;
import com.karunamay.airlink.dto.role.RoleRequestDTO;
import com.karunamay.airlink.dto.role.RoleResponseDTO;
import com.karunamay.airlink.dto.role.RoleUpdateRequestDTO;
import com.karunamay.airlink.service.user.RoleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/admin/role")
@RequiredArgsConstructor
@Tag(name = "Role",
        description = "CRUD operations for managing user roles and permissions. Requires elevated privileges.")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleServiceImpl roleService;

    @Operation(
            summary = "Get role by ID",
            description = "Retrieves a single role definition, including its associated permissions.",
            tags = {"Role Management (Admin)"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Role retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponseDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Role does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<RoleResponseDTO>> getRoleById(@PathVariable Long id) {
        log.info("REST: Fetch role by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(roleService.getRoleById(id)));
    }

    @Operation(
            summary = "Get all roles",
            description = "Retrieves a list of all defined roles in the system.",
            tags = {"Role Management (Admin)"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "List of roles retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoleResponseDTO.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<RestApiResponse<List<RoleResponseDTO>>> getAllRoles() {
        log.info("REST: Fetch all roles");
        return ResponseEntity.ok(RestApiResponse.success(roleService.getAllRoles()));
    }


    @Operation(
            summary = "Create a new role",
            description = "Creates a new user role with specified name and permissions.",
            tags = {"Role Management (Admin)"}
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Role creation data",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RoleRequestDTO.class),
                    examples = @ExampleObject(name = "New Role Example", value = """
                                {
                                  "name": "ROLE_MANAGER",
                                  "description": "User with management privileges",
                                  "permissionNames": ["user:read", "booking:read"]
                                }
                            """)
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Role created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponseDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or Role name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<RestApiResponse<RoleResponseDTO>> createRole(@Valid @RequestBody RoleRequestDTO requestDTO) {
        log.info("REST: Create new role");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestApiResponse.success(roleService.createRole(requestDTO)));
    }


    @Operation(
            summary = "Update an existing role",
            description = "Updates the name, description, and/or permissions of an existing role.",
            tags = {"Role Management (Admin)"}
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Role update data",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RoleUpdateRequestDTO.class)
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Role updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoleResponseDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Role to update does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestApiResponse<RoleResponseDTO>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequestDTO requestDTO
    ) {
        log.info("REST: Update role by id {}", id);
        return ResponseEntity.ok(RestApiResponse.success(roleService.updateRole(id, requestDTO)));
    }

    @Operation(
            summary = "Delete a role",
            description = "Permanently deletes a role by ID. Note: Deletion may fail if the role is currently assigned to users.",
            tags = {"Role Management (Admin)"}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Role deleted successfully (No Content)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Role to delete does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Role cannot be deleted (e.g., currently assigned to users)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteRole(@PathVariable Long id) {
        log.info("REST: Delete role by id {}", id);
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
