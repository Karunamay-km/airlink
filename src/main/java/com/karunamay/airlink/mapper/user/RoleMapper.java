package com.karunamay.airlink.mapper.user;

import com.karunamay.airlink.dto.role.RoleRequestDTO;
import com.karunamay.airlink.dto.role.RoleResponseDTO;
import com.karunamay.airlink.dto.role.RoleUpdateRequestDTO;
import com.karunamay.airlink.model.user.Permission;
import com.karunamay.airlink.model.user.Role;
import com.karunamay.airlink.repository.user.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;

    public RoleResponseDTO toBasicDTO(Role role) {
        if (role == null) return null;
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.getActive())
                .systemRole(role.getSystemRole())
                .build();
    }

    public RoleResponseDTO toResponseDTO(Role role) {
        if (role == null) return null;
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.getActive())
                .systemRole(role.getSystemRole())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .permissions(role.getPermissions()
                        .stream()
                        .map(permissionMapper::toPermissionResponseDTO)
                        .collect(Collectors.toSet()))
                .build();
    }

    public Role toEntity(RoleRequestDTO requestDTO) {

        Set<Permission> permissions = !requestDTO.getPermissionNames().isEmpty()
                ? permissionRepository.findByNameIn(requestDTO.getPermissionNames())
                : new HashSet<>();

        Role role = Role.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .active(requestDTO.getActive())
                .systemRole(requestDTO.getSystemRole())
                .build();

        permissions.forEach(role::addPermission);

        return role;
    }


    public void updateEntityFromRequest(Role role, RoleUpdateRequestDTO requestDTO) {
        if (requestDTO.getName() != null) {
            role.setName(requestDTO.getName());
        }
        if (requestDTO.getDescription() != null) {
            role.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getActive() != null) {
            role.setActive(requestDTO.getActive());
        }
        if (requestDTO.getSystemRole() != null) {
            role.setSystemRole(requestDTO.getSystemRole());
        }
        if (requestDTO.getPermissionNames() != null) {
            Set<Permission> newPermissions = permissionRepository.findByNameIn(requestDTO.getPermissionNames());
            role.getPermissions().retainAll(newPermissions);
            role.getPermissions().addAll(newPermissions);
        }
    }
}
