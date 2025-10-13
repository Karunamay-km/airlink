package com.karunamay.airlink.service.user;

import com.karunamay.airlink.dto.role.RoleRequestDTO;
import com.karunamay.airlink.dto.role.RoleResponseDTO;
import com.karunamay.airlink.dto.role.RoleUpdateRequestDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.user.RoleMapper;
import com.karunamay.airlink.model.user.Permission;
import com.karunamay.airlink.model.user.Role;
import com.karunamay.airlink.repository.user.RoleRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final BaseService baseService;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponseDTO getRoleById(Long id) {
        log.info("Fetch role for id {}", id);
        return roleMapper
                .toResponseDTO(
                        baseService.findByIdOrThrow(id, roleRepository)
                );
    }

    @Override
    public RoleResponseDTO getRoleByName(String name) {
        log.info("Fetch role for name {}", name);
        Role role = roleRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role with name " + name + " not found"));
        return roleMapper.toResponseDTO(role);
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        log.info("Fetch all roles");
        return roleRepository.findAll().stream().map(roleMapper::toResponseDTO).toList();
    }

    @Override
    public RoleResponseDTO createRole(RoleRequestDTO requestDTO) {
        log.info("Create new role");
        Role role = roleMapper.toEntity(requestDTO);
        if (!requestDTO.getPermissionNames().isEmpty()) {
            findPermissionsOrThrow(role, requestDTO.getPermissionNames());
        }

        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponseDTO(savedRole);
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleUpdateRequestDTO requestDTO) {
        log.info("Update role by id {}", id);
        Role role = baseService.findByIdOrThrow(id, roleRepository);

        if (requestDTO.getName() != null && !requestDTO.getName().equalsIgnoreCase(role.getName())) {
            if (roleRepository.existsByName(requestDTO.getName())) {
                throw new DuplicateResourceException("Role with name " + requestDTO.getName() + " already exists");
            }
        }

        roleMapper.updateEntityFromRequest(role, requestDTO);

        if (requestDTO.getPermissionNames() != null && !requestDTO.getPermissionNames().isEmpty()) {
            findPermissionsOrThrow(role, requestDTO.getPermissionNames());
        }

        Role updatedRole = roleRepository.save(role);

        log.info("Role update successfully id {}", id);
        return roleMapper.toResponseDTO(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Delete role by id {}", id);
        Role role = baseService.findByIdOrThrow(id, roleRepository);
        roleRepository.delete(role);
        log.info("Role deleted successfully id {}", id);
    }

    private <T extends RoleRequestDTO> void findPermissionsOrThrow(Role role, Set<String> permissionName) {
        Set<String> foundPermissionNames = role
                .getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
        Set<String> missingPermission = new HashSet<>();
        if (role.getPermissions().size() != permissionName.size()) {
            Set<String> name = permissionName
                    .stream()
                    .filter(n -> !foundPermissionNames.contains(n))
                    .collect(Collectors.toSet());
            missingPermission.addAll(name);
        }
        if (!missingPermission.isEmpty()) {
            StringBuilder errorString = new StringBuilder();
            missingPermission.forEach(p ->
                    errorString.append("Permission with name ")
                            .append(p)
                            .append(" not found. "));
            throw new ResourceNotFoundException(errorString.toString());
        }
    }
}
