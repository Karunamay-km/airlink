package com.karunamay.airlink.mapper.user;

import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionRequestDTO;
import com.karunamay.airlink.dto.permission.PermissionResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionUpdateRequestDTO;
import com.karunamay.airlink.mapper.PageMapper;
import com.karunamay.airlink.model.user.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PermissionMapper {

    private final PageMapper pageMapper;

    public PermissionResponseDTO toPermissionResponseDTO(Permission permission) {
        if (permission == null) return null;
        return PermissionResponseDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .active(permission.getActive())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }

    public Permission toEntity(PermissionRequestDTO permission) {
        if (permission == null) return null;
        return Permission.builder()
                .name(permission.getName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .active(permission.getActive())
                .build();
    }

    public PageResponseDTO<PermissionResponseDTO> toPageResponse(Page<Permission> permissionPage) {
        return pageMapper.toPageResponse(permissionPage, this::toPermissionResponseDTO);

    }

    public void updateEntityFromRequest(Permission permission, PermissionUpdateRequestDTO requestDTO) {
        if (permission == null || requestDTO == null) return;
        if (requestDTO.getResource() != null) {
            permission.setResource(requestDTO.getResource());
            permission.setName(requestDTO.getResource() + ":" + permission.getAction());
        }
        if (requestDTO.getAction() != null) {
            permission.setAction(requestDTO.getAction());
            permission.setName(permission.getResource() + ":" + requestDTO.getAction());
        }
        if (requestDTO.getDescription() != null) {
            permission.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getActive() != null) {
            permission.setActive(requestDTO.getActive());
        }
    }

}
