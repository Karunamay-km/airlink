package com.karunamay.airlink.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponseDTO {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Error timestamp")
    @NotNull
    private LocalDateTime timestamp;

    @Schema(description = "Error status")
    @NotNull
    private int status;

    @Schema(description = "Error type")
    @NotNull
    private String error;

    @Schema(description = "Error message")
    @NotNull
    private String message;

    @Schema(description = "Error path")
    @NotNull
    private String path;

    @Schema(description = "Input validation errors")
    @NotNull
    private List<ValidationError> validationErrors;

    @Schema(description = "Error details")
    @NotNull
    private Map<String, String> details;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidationError {

        @NotNull
        private String field;

        @NotNull
        private String message;

        @NotNull
        private Object rejectedValue;
    }
}
