package com.karunamay.airlink.service.user;

import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.user.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    AuthenticationResponseDTO registerUser(UserRegistrationRequestDTO requestDTO);

    UserResponseDTO getUserById(Long id);

    UserDetailResponseDTO getUserDetailsById(Long id);

    UserResponseDTO getUserByUsername(String username);

    UserResponseDTO getUserByEmail(String email);

    PageResponseDTO<UserResponseDTO> getAllUsers(Pageable pageable);

    UserResponseDTO updateUser(Long userId, UserUpdateRequestDTO requestDTO);

    AuthenticationResponseDTO authenticateUser(UserLoginRequestDTO credentials);

    UserResponseDTO checkAuth(String accessToken);

    AuthenticationResponseDTO refreshToken(String refreshToken);

    void changePassword(Long id, PasswordChangeRequestDTO requestDTO);

    void resetPassword(String email);

    void logout(String accessToken, String refreshToken);

    UserResponseDTO assignRole(Long userId, Long roleId);

    UserResponseDTO removeRole(Long userId, Long roleId);

    UserResponseDTO assignRoles(Long userId, List<Long> roleIds);

    List<UserResponseDTO> getUsersByRole(String roleName);

    void sendEmailVerification(Long userId);

    void verifyEmail(String token);


}
