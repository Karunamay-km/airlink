package com.karunamay.airlink.service.permission;

import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionRequestDTO;
import com.karunamay.airlink.dto.permission.PermissionResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionUpdateRequestDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {

    PermissionResponseDTO createPermission(PermissionRequestDTO requestDTO);

    PermissionResponseDTO getPermissionById(Long id);

    List<PermissionResponseDTO> getPermissionsByResource(String resource);

    List<PermissionResponseDTO> getPermissionsByName(String name);

    List<PermissionResponseDTO> getPermissionsByAction(String action);

    PageResponseDTO<PermissionResponseDTO> getAllPermissions(Pageable pageable);

    List<String> getAllPermissionResource();

    List<String> getAllPermissionAction();

    PermissionResponseDTO updatePermission(Long id, PermissionUpdateRequestDTO requestDTO);

    void deletePermission(Long id);
}
