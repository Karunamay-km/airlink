package com.karunamay.airlink.mapper.user;

import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.user.UserDetailResponseDTO;
import com.karunamay.airlink.dto.user.UserRegistrationRequestDTO;
import com.karunamay.airlink.dto.user.UserResponseDTO;
import com.karunamay.airlink.dto.user.UserUpdateRequestDTO;
import com.karunamay.airlink.mapper.PageMapper;
import com.karunamay.airlink.model.user.Permission;
import com.karunamay.airlink.model.user.Role;
import com.karunamay.airlink.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleMapper roleMapper;
    private final PageMapper pageMapper;

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .dob(user.getDob())
                .enabled(user.getEnabled())
                .emailVerified(user.getEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(extractUserRoleNames(user.getRoles()))
                .permissions(extractUserPermissionNames(user))
                .build();
    }

    public UserResponseDTO toBasicResponseDTO(User user) {
        if (user == null) return null;
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .dob(user.getDob())
                .build();
    }

    public UserDetailResponseDTO toDetailResponseDTO(User user) {
        if (user == null) return null;
        return UserDetailResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .dob(user.getDob())
                .enabled(user.getEnabled())
                .emailVerified(user.getEmailVerified())
                .accountNonExpired(user.getAccountNonExpired())
                .accountNonLocked(user.getAccountNonLocked())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(user.getRoles()
                        .stream()
                        .map(roleMapper::toResponseDTO)
                        .collect(Collectors.toSet()))
                .build();
    }

    public User toEntity(UserRegistrationRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        return User.builder()
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .password(requestDTO.getPassword())
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .phone(requestDTO.getPhone())
                .dob(requestDTO.getDob())
                .build();
    }

    public void updateEntityFromRequest(User user, UserUpdateRequestDTO requestDTO) {
        if (user == null || requestDTO == null) return;
        if (requestDTO.getUsername() != null) {
            user.setUsername(requestDTO.getUsername());
        }
        if (requestDTO.getEmail() != null) {
            user.setEmail(requestDTO.getEmail());
        }
        if (requestDTO.getFirstName() != null) {
            user.setFirstName(requestDTO.getFirstName());
        }
        if (requestDTO.getLastName() != null) {
            user.setLastName(requestDTO.getLastName());
        }
        if (requestDTO.getPhone() != null) {
            user.setPhone(requestDTO.getPhone());
        }
        if (requestDTO.getDob() != null) {
            user.setDob(requestDTO.getDob());
        }
    }

    public PageResponseDTO<UserResponseDTO> toPageResponse(Page<User> userPage) {
        return pageMapper.toPageResponse(userPage, this::toResponseDTO);
    }

    private Set<String> extractUserRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> extractUserPermissionNames(User user) {
        return user.getRoles()
                .stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

    }

}
