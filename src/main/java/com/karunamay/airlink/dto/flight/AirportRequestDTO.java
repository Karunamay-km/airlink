package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirportRequestDTO {

    @NotBlank(message = "Code is required", groups = OnCreate.class)
    private String code;

    @NotBlank(message = "Name is required", groups = OnCreate.class)
    private String name;

    private Boolean active;

    private String city;
}
