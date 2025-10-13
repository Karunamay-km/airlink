package com.karunamay.airlink.controller.user;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.user.*;
import com.karunamay.airlink.service.user.UserService;

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
public class UserController {

    public final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long id
    ) {
        log.info("REST: Get user by ID request: {}", id);
        UserResponseDTO userResponseDTO = userService.getUserById(id);
        return ResponseEntity.ok(RestApiResponse.success(userResponseDTO));
    }


    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and #id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO request
    ) {
        log.info("REST: Update user by ID request: {}", id);
        UserResponseDTO userResponseDTO = userService.updateUser(id, request);
        return ResponseEntity.ok(RestApiResponse.success("User updated successfully", userResponseDTO));
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("isAuthenticated() and #id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<Void>> changePassword(
            @RequestParam Long id, @Valid @RequestBody PasswordChangeRequestDTO request
    ) {
        log.info("REST: Change password request received for user id: {}", id);
        userService.changePassword(id, request);
        return ResponseEntity.ok(RestApiResponse.success("Password change successfully", null));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<PageResponseDTO<UserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info("REST: Get all users request - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        PageResponseDTO<UserResponseDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(RestApiResponse.success(users));
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserDetailResponseDTO>> getUserDetails(
            @PathVariable Long id
    ) {
        log.info("REST: Get user details request for ID: {}", id);
        UserDetailResponseDTO user = userService.getUserDetailsById(id);
        return ResponseEntity.ok(RestApiResponse.success(user));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> assignRole(
            @PathVariable Long userId,
            @PathVariable Long roleId
    ) {
        log.info("REST: Assign role {} to user {}", roleId, userId);
        UserResponseDTO user = userService.assignRole(userId, roleId);
        return ResponseEntity.ok(RestApiResponse.success("Role applied successfully", user));
    }


    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> removeRole(
            @PathVariable Long userId,
            @PathVariable Long roleId
    ) {
        log.info("REST: Delete role {} to user {}", roleId, userId);
        UserResponseDTO user = userService.removeRole(userId, roleId);
        return ResponseEntity.ok(RestApiResponse.success("Role removed successfully", user));
    }
}
