package com.karunamay.airlink.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponseDTO {

    @NotBlank
    @Schema(required = true)
    private String accessToken;

    @NotBlank
    @Schema(required = true)
    private String refreshToken;

    @NotBlank
    @Schema(required = true)
    private String tokenType;

    @NotBlank
    @Schema(required = true)
    private Long expiresIn;

    @NotBlank
    @Schema(required = true)
    private UserResponseDTO user;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotBlank
    @Schema(required = true)
    private LocalDateTime timestamp;
}
