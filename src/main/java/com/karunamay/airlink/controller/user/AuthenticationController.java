package com.karunamay.airlink.controller.user;

import com.karunamay.airlink.dto.api.RestApiResponse;
import com.karunamay.airlink.dto.error.ErrorResponseDTO;
import com.karunamay.airlink.dto.user.*;
import com.karunamay.airlink.service.security.JwtTokenProvider;
import com.karunamay.airlink.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
@Tag(name = "Authentication", description = "APIs for authenticate users")
public class AuthenticationController {

    public final UserService userService;
    public final JwtTokenProvider jwt;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account. No authentication required.",
            tags = {"Authentication"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User registered successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserRegistrationResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Username or email already exists",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration details",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RegistrationRequestDTO.class)
            )
    )
    @PostMapping("/register")
    public ResponseEntity<
            RestApiResponse<RegistrationResponseDTO>
            > registerUser(@Valid @RequestBody RegistrationRequestDTO request) {
        log.info(
                "REST: Register user request received for username: {}",
                request.getUsername()
        );
        RegistrationResponseDTO user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestApiResponse.success("User registered successfully", user)
        );
    }

    @Operation(
            summary = "User login",
            description = "Authenticates user credentials and issues new JWT access and refresh tokens via **HTTP-only cookies** " +
                    "and in the response body.",
            tags = {"Authentication"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful. Cookies set.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = RestAuthenticationResponseDTO.class
                                    ),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "message": "Login successful",
                                                      "data": {
                                                        "accessToken": "eyJ...",
                                                        "refreshToken": "eyJ...",
                                                        "tokenType": "Bearer",
                                                        "expiresIn": 18000
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials.",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = UserLoginRequestDTO.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "email": "admin@example.com",
                                      "password": "Admin@123"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/login")
    public ResponseEntity<RestApiResponse<AuthenticationResponseDTO>> login(
            @Valid @RequestBody UserLoginRequestDTO request
    ) {
        log.info(
                "REST: Login user request received for email: {}",
                request.getEmail()
        );
        AuthenticationResponseDTO authenticateUser =
                userService.authenticateUser(request);
        ResponseCookie accessTokenCookie = ResponseCookie.from(
                        "accessToken",
                        authenticateUser.getAccessToken()
                )
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(18000L)
                .sameSite("none")
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                        "refreshToken",
                        authenticateUser.getRefreshToken()
                )
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3600000L)
                .sameSite("none")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(
                        RestApiResponse.success("Login successful", authenticateUser)
                );
    }

    @Operation(
            summary = "Refresh Access Token",
            description = "Uses the refresh token (from query param or cookie) to generate a **new access token**. A new access token cookie is set.",
            tags = {"Authentication"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token refreshed successfully. New access token returned in body and cookie.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = RestAuthenticationResponseDTO.class
                                    ),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "message": "Token refreshed successfully",
                                                      "data": {
                                                        "accessToken": "eyJ...",
                                                        "tokenType": "Bearer",
                                                        "expiresIn": 18000
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid or expired refresh token.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
            }
    )
    @Parameter(
            in = ParameterIn.QUERY,
            name = "token",
            description = "Optional: Refresh token passed as a query parameter (overrides cookie).",
            schema = @Schema(type = "string")
    )
    @Parameter(
            in = ParameterIn.COOKIE,
            name = "refreshToken",
            description = "Optional: Refresh token passed as an HTTP-only cookie.",
            schema = @Schema(type = "string")
    )
    @GetMapping("/refresh")
    public ResponseEntity<
            RestApiResponse<AuthenticationResponseDTO>
            > refreshToken(
            @RequestParam(name = "token", required = false) String refreshToken,
            @CookieValue(
                    name = "refreshToken",
                    required = false
            ) String refreshTokenCookie
    ) {
        log.info("REST: Refresh token request received");

        String token = refreshToken != null ? refreshToken : refreshTokenCookie;

        AuthenticationResponseDTO authResponse = userService.refreshToken(
                token
        );
        ResponseCookie accessTokenCookie = ResponseCookie.from(
                        "accessToken",
                        authResponse.getAccessToken()
                )
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(18000L)
                .sameSite("none")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body(
                        RestApiResponse.success(
                                "Token refreshed successfully",
                                authResponse
                        )
                );
    }

    @Operation(
            summary = "Forgot Password",
            description = "Initiates the password reset process by sending an email with instructions to the provided address.",
            tags = {"Authentication"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password reset instructions sent. Success message returned (email delivery assumed).",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = RestAuthenticationVoidResponseDTO.class
                                    ),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "message": "Password reset instruction sent to email",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User with the provided email not found.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
            }
    )
    @Parameter(
            in = ParameterIn.QUERY,
            name = "email",
            description = "The email address of the user who requested the password reset.",
            required = true,
            schema = @Schema(type = "string", format = "email")
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<RestApiResponse<Void>> forgotPassword(
            @RequestParam String email
    ) {
        log.info("REST: Forgot password request received for email: {}", email);
        userService.resetPassword(email);
        return ResponseEntity.ok(
                RestApiResponse.success(
                        "Password reset instruction sent to email",
                        null
                )
        );
    }

    @Operation(
            summary = "Check Authentication Status",
            description = "Validates the active session's access token and returns the current user's details.",
            tags = {"Authentication"},
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token is valid. User data returned.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = RestUserResponseDTO.class
                                    ),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "message": "Success",
                                                      "data": {
                                                        "id": 1,
                                                        "username": "johndoe",
                                                        "email": "john@example.com",
                                                        "roles": ["ROLE_USER"]
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid or missing access token.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
            }
    )
    @Parameter(
            in = ParameterIn.COOKIE,
            name = "accessToken",
            description = "The HTTP-only access token cookie.",
            required = true,
            schema = @Schema(type = "string")
    )
    @GetMapping("/check-auth")
    public ResponseEntity<RestApiResponse<UserResponseDTO>> checkAuth(
            @CookieValue(required = false) String accessToken
    ) {
        UserResponseDTO user = userService.checkAuth(accessToken);
        return ResponseEntity.ok(RestApiResponse.success(user));
    }

    @Operation(
            summary = "Logout user",
            description = "Logout users by marking the user's accessToken as blacklisted.",
            tags = {"Authentication"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "No content. User successfully logout"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid or missing access token.",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDTO.class)
                            )
                    ),
            }
    )
    @Parameter(
            in = ParameterIn.COOKIE,
            name = "accessToken",
            description = "The HTTP-only access token cookie.",
            required = true,
            schema = @Schema(type = "string")
    )
    @Parameter(
            in = ParameterIn.COOKIE,
            name = "refreshToken",
            description = "The HTTP-only refresh token cookie.",
            required = true,
            schema = @Schema(type = "string")
    )
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(required = false) String accessToken,
            @CookieValue(required = false) String refreshToken
    ) {
        userService.logout(accessToken, refreshToken);
        ResponseCookie accessResponseCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("none")
                .build();
        ResponseCookie refreshResponseCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("none")
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, accessResponseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshResponseCookie.toString())
                .build();
    }

    private static class UserRegistrationResponseDTO
            extends RestApiResponse<RegistrationResponseDTO> {
    }


}
