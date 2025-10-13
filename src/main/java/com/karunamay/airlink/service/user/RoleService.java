package com.karunamay.airlink.service.user;

import com.karunamay.airlink.dto.role.RoleRequestDTO;
import com.karunamay.airlink.dto.role.RoleResponseDTO;
import com.karunamay.airlink.dto.role.RoleUpdateRequestDTO;

import java.util.List;

public interface RoleService {

    RoleResponseDTO getRoleById(Long id);

    RoleResponseDTO getRoleByName(String name);

    List<RoleResponseDTO> getAllRoles();

    RoleResponseDTO createRole(RoleRequestDTO requestDTO);

    RoleResponseDTO updateRole(Long id, RoleUpdateRequestDTO requestDTO);

    void deleteRole(Long id);

}
