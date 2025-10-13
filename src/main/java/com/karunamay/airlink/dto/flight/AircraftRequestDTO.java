package com.karunamay.airlink.dto.flight;

import com.karunamay.airlink.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AircraftRequestDTO {

    @NotBlank(message = "Model is required", groups = OnCreate.class)
    private String model;

    @NotBlank(message = "Registration number is required", groups = OnCreate.class)
    private String registrationNumber;

    @NotNull(message = "Capacity is required", groups = OnCreate.class)
    private Integer capacity;

    private Boolean active;
    private Long airlineId;

}
