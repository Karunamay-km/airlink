package com.karunamay.airlink.dto.role;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleRequestDTO {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50)
    private String name;

    private String description;
    private Boolean systemRole;
    private Boolean active;
    private Set<String> permissionNames;
}
