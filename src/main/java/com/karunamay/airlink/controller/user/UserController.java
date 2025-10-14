package com.karunamay.airlink.controller.user;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.user.*;
import com.karunamay.airlink.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/users")
@Tag(name = "User", description = "Operations for retrieving and managing user profiles, roles, and security.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    public final UserService userService;

    @Operation(summary = "Get User Profile by ID", description = "Retrieves the basic profile information of a user. Accessible by the user themselves or any authenticated user.", responses = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long id) {
        log.info("REST: Get user by ID request: {}", id);
        UserResponseDTO userResponseDTO = userService.getUserById(id);
        return ResponseEntity.ok(RestApiResponse.success(userResponseDTO));
    }

    @Operation(summary = "Update User Profile", description = "Updates the basic profile information of a user. Accessible by the user themselves or an ADMIN.", responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden (Not the user or not ADMIN)"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and #id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO request) {
        log.info("REST: Update user by ID request: {}", id);
        UserResponseDTO userResponseDTO = userService.updateUser(id, request);
        return ResponseEntity.ok(RestApiResponse.success("User updated successfully", userResponseDTO));
    }

    @Operation(summary = "Change User Password", description = "Allows a user to change their password by providing the old and new passwords. Accessible by the user themselves or an ADMIN.", responses = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password format or old password mismatch"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}/change-password")
    @PreAuthorize("isAuthenticated() and #id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<Void>> changePassword(
            @PathVariable(name = "id") Long id, @Valid @RequestBody PasswordChangeRequestDTO request) {
        log.info("REST: Change password request received for user id: {}", id);
        userService.changePassword(id, request);
        return ResponseEntity.ok(RestApiResponse.success("Password change successfully", null));
    }

    @Operation(summary = "Get All Users (Paginated)", description = "Retrieves a paginated list of all registered users. **Requires ADMIN role.**", responses = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully", content = @Content(schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden (Requires ADMIN role)")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<PageResponseDTO<UserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        log.info("REST: Get all users request - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        PageResponseDTO<UserResponseDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(RestApiResponse.success(users));
    }

    @Operation(summary = "Get Full User Details", description = "Retrieves complete user details, including associated roles and permissions. **Requires ADMIN role.**", responses = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully", content = @Content(schema = @Schema(implementation = UserDetailResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden (Requires ADMIN role)"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}/details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserDetailResponseDTO>> getUserDetails(
            @PathVariable Long id) {
        log.info("REST: Get user details request for ID: {}", id);
        UserDetailResponseDTO user = userService.getUserDetailsById(id);
        return ResponseEntity.ok(RestApiResponse.success(user));
    }

    @Operation(summary = "Assign Role to User", description = "Assigns a specific role to a user. **Requires ADMIN role.**", responses = {
            @ApiResponse(responseCode = "200", description = "Role applied successfully", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden (Requires ADMIN role)"),
            @ApiResponse(responseCode = "404", description = "User or Role not found")
    })
    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> assignRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        log.info("REST: Assign role {} to user {}", roleId, userId);
        UserResponseDTO user = userService.assignRole(userId, roleId);
        return ResponseEntity.ok(RestApiResponse.success("Role applied successfully", user));
    }

    @Operation(summary = "Remove Role from User", description = "Removes a specific role from a user. **Requires ADMIN role.**", responses = {
            @ApiResponse(responseCode = "200", description = "Role removed successfully", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden (Requires ADMIN role)"),
            @ApiResponse(responseCode = "404", description = "User or Role not found")
    })
    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> removeRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        log.info("REST: Delete role {} to user {}", roleId, userId);
        UserResponseDTO user = userService.removeRole(userId, roleId);
        return ResponseEntity.ok(RestApiResponse.success("Role removed successfully", user));
    }
}
