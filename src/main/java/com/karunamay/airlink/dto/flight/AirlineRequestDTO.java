package com.karunamay.airlink.dto.flight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.model.flight.Aircraft;
import com.karunamay.airlink.model.flight.Flight;
import com.karunamay.airlink.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirlineRequestDTO {

    @NotBlank(message = "Code is required", groups = OnCreate.class)
    private String code;

    @NotBlank(message = "Name is required", groups = OnCreate.class)
    private String name;

    private String logoUrl;

    private Boolean active;

}
