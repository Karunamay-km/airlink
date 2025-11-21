package com.karunamay.airlink.service.permission;


import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionRequestDTO;
import com.karunamay.airlink.dto.permission.PermissionResponseDTO;
import com.karunamay.airlink.dto.permission.PermissionUpdateRequestDTO;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.exceptions.ValidationException;
import com.karunamay.airlink.mapper.user.PermissionMapper;
import com.karunamay.airlink.model.user.Permission;
import com.karunamay.airlink.repository.user.PermissionRepository;
import com.karunamay.airlink.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final BaseService baseService;

    @Override
    public PermissionResponseDTO createPermission(PermissionRequestDTO requestDTO) {
        log.info("Creating new permission with name {}", requestDTO.getName());
        if (permissionRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Permission name {} " + requestDTO.getName() + " already exists");
        }

        if (!requestDTO.getName().contains(":") && (requestDTO.getResource() == null || requestDTO.getAction() == null)) {
            Map<String, String> error = new HashMap<>(Map.of(
                    "name",
                    "Name should have the pattern of [resource:action]. Or please provide resource and action explicitly")
            );
            throw new ValidationException("Invalid name property", error);
        }

        Permission permission = permissionMapper.toEntity(requestDTO);
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created successfully with id: {}", savedPermission.getId());
        return permissionMapper.toPermissionResponseDTO(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDTO getPermissionById(Long id) {
        log.info("Fetching permission by id {}", id);
        Permission permission = baseService.findByIdOrThrow(id, permissionRepository);
        return permissionMapper.toPermissionResponseDTO(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDTO> getPermissionsByResource(String resource) {
        log.info("Fetching permissions by resource {}", resource);
        return permissionRepository.findByResource(resource)
                .stream()
                .map(permissionMapper::toPermissionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDTO> getPermissionsByName(String name) {
        log.info("Fetching permissions by name {}", name);
        return permissionRepository.findByName(name)
                .stream()
                .map(permissionMapper::toPermissionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDTO> getPermissionsByAction(String action) {
        log.info("Fetching permissions by action {}", action);
        return permissionRepository.findByAction(action)
                .stream()
                .map(permissionMapper::toPermissionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PermissionResponseDTO> getAllPermissions(Pageable pageable) {
        log.debug("Fetching all permissions");
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        return permissionMapper.toPageResponse(permissionPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllPermissionResource() {
        log.debug("Fetching all permission resources");
        return permissionRepository.findAllResources();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllPermissionAction() {
        log.debug("Fetching all permission actions");
        return permissionRepository.findAllActions();
    }

    @Override
    public PermissionResponseDTO updatePermission(Long id, PermissionUpdateRequestDTO requestDTO) {
        log.info("Update request for permission id {} ", id);

        Permission permission = baseService.findByIdOrThrow(id, permissionRepository);

        String name = requestDTO.getResource() + ":" + requestDTO.getAction();

        if (!name.equalsIgnoreCase(permission.getName())) {
            if (permissionRepository.existsByName(name)) {
                throw new DuplicateResourceException("Permission name " + name + " already exists");
            }
        }

        permissionMapper.updateEntityFromRequest(permission, requestDTO);
        Permission updatedPermission = permissionRepository.save(permission);

        log.info("Permission updated successfully id {} ", id);
        return permissionMapper.toPermissionResponseDTO(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {
        log.info("Delete request for role id {}", id);
        Permission permission = baseService.findByIdOrThrow(id, permissionRepository);
        permissionRepository.delete(permission);
        log.info("Permission deleted successfully id {}", id);
    }
}
