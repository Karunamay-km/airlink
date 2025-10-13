package com.karunamay.airlink.dto.role;

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
public class RoleUpdateRequestDTO {

    @Size(min = 2, max = 8, message = "Role name must be between 2 and 8 characters")
    private String name;

    private String description;
    private Boolean systemRole;
    private Boolean active;
    private Set<String> permissionNames;
}
