package com.karunamay.airlink.service.user;

import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import com.karunamay.airlink.dto.user.*;
import com.karunamay.airlink.exceptions.BusinessException;
import com.karunamay.airlink.exceptions.DuplicateResourceException;
import com.karunamay.airlink.exceptions.JwtAuthenticationException;
import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import com.karunamay.airlink.mapper.user.UserMapper;
import com.karunamay.airlink.model.token.BlackListToken;
import com.karunamay.airlink.model.user.Role;
import com.karunamay.airlink.model.user.User;
import com.karunamay.airlink.repository.token.BlackListTokenRepository;
import com.karunamay.airlink.repository.user.RoleRepository;
import com.karunamay.airlink.repository.user.UserRepository;
import com.karunamay.airlink.service.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final BlackListTokenRepository blackListTokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwt;

    @Override
    public RegistrationResponseDTO registerUser(
            RegistrationRequestDTO requestDTO
    ) {
        log.info(
                "Registering new user with username {} and password {}",
                requestDTO.getUsername(),
                requestDTO.getPassword()
        );

        User user = userMapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        Role defaultRole = roleRepository
                .findByName("ROLE_USER")
                .orElseThrow(() ->
                        new ResourceNotFoundException("Default role not found")
                );
        user.addRole(defaultRole);

        userRepository.save(user);

        log.info(
                "User registered successfully with id: {} username: {}",
                user.getId(),
                user.getUsername()
        );

        return RegistrationResponseDTO.builder().build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        User user = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user with id: " + id + " not found"
                        )
                );
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponseDTO getUserDetailsById(Long id) {
        log.debug("Fetching user detail by id: {}", id);
        User user = findUserByIdOrThrow(id);
        return userMapper.toDetailResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        log.debug("Fetching user detail by username: {}", username);
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user with username: " + username + " not found"
                        )
                );
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        log.debug("Fetching user detail by email: {}", email);
        User user = userRepository
                .findByUsername(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user with email: " + email + " not found"
                        )
                );
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<UserResponseDTO> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users");
        Page<User> userPage = userRepository.findAllActive(pageable);
        return userMapper.toPageResponse(userPage);
    }

    @Override
    public UserResponseDTO updateUser(
            Long userId,
            UserUpdateRequestDTO requestDTO
    ) {
        User user = findUserByIdOrThrow(userId);
        if (
                requestDTO.getUsername() != null &&
                        !requestDTO.getUsername().equalsIgnoreCase(user.getUsername())
        ) {
            if (userRepository.existsByUsername(requestDTO.getUsername())) {
                throw new DuplicateResourceException(
                        "Username already exists: " + requestDTO.getUsername()
                );
            }
        }
        if (
                requestDTO.getEmail() != null &&
                        !requestDTO.getEmail().equalsIgnoreCase(user.getEmail())
        ) {
            if (userRepository.existsByEmail(requestDTO.getEmail())) {
                throw new DuplicateResourceException(
                        "Email already exists: " + requestDTO.getEmail()
                );
            }
        }

        userMapper.updateEntityFromRequest(user, requestDTO);
        User updateUser = userRepository.save(user);

        log.info("User update successfully id {}", user.getId());
        return userMapper.toResponseDTO(updateUser);
    }

    @Override
    public AuthenticationResponseDTO authenticateUser(
            UserLoginRequestDTO credentials
    ) {
        log.info("Authenticating user: {}", credentials.getEmail());
        User user = userRepository
                .findByEmail(credentials.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid Credentials"));

        if (!user.isAccountNonLocked()) {
            throw new BusinessException("Account is locked");
        }

        if (
                !passwordEncoder.matches(
                        credentials.getPassword(),
                        user.getPassword()
                )
        ) {
            throw new BusinessException("Invalid Credentials");
        }

        user.updateLastLogin();
        userRepository.save(user);

        String accessToken = jwt.generateAccessToken(user.getUsername());
        String refreshToken = jwt.generateRefreshToken(user.getUsername());
        Long expiresIn =
                jwt.getExpirationDateFromToken(accessToken).getTime() / 1000;
        UserResponseDTO userResponseDTO = userMapper.toBasicResponseDTO(user);

        AuthenticationResponseDTO authenticationResponse =
                AuthenticationResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(expiresIn)
                        .user(userResponseDTO)
                        .build();

        log.info("User authenticated successfully: {}", user.getEmail());
        return authenticationResponse;
    }

    @Override
    public UserResponseDTO checkAuth(String accessToken) {
        Optional<BlackListToken> isBlackListedToken =
                blackListTokenRepository.findByTokenId(accessToken);
        if (accessToken == null || accessToken.isBlank() || isBlackListedToken.isPresent()) {
            throw new BusinessException("Invalid credentials. Please login.");
        }
        Claims claims = jwt.validateAndParseClaims(accessToken).getPayload();
        String username = claims.getSubject();
        return userMapper.toBasicResponseDTO(
                findUserByUsernameOrThrow(username)
        );
    }

    @Override
    public AuthenticationResponseDTO refreshToken(String refreshToken) {
        log.info("Refreshing token");

        if (refreshToken == null || !jwt.isTokenValid(refreshToken)) {
            throw new JwtAuthenticationException(
                    "Invalid or expired refresh token"
            );
        }

        String username = jwt
                .validateAndParseClaims(refreshToken)
                .getPayload()
                .getSubject();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isEnabled() || !user.isAccountNonLocked()) {
            throw new BusinessException(
                    "User is not active or has locked account"
            );
        }

        String newAccessToken = jwt.generateAccessToken(username);
        Long expiresIn =
                jwt.getExpirationDateFromToken(newAccessToken).getTime() / 1000;

        log.info("Token refreshed successfully for user: {}", username);

        return AuthenticationResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(userMapper.toResponseDTO(user))
                .build();
    }

    @Override
    public void changePassword(Long id, PasswordChangeRequestDTO requestDTO) {
        log.info("Change password for user with id: {}", id);

        if (
                !requestDTO
                        .getNewPassword()
                        .equalsIgnoreCase(requestDTO.getConfirmPassword())
        ) {
            throw new BusinessException("Both passwords didn't match");
        }

        User user = findUserByIdOrThrow(id);

        if (!user.isAccountNonLocked()) {
            throw new BusinessException("Account is locked");
        }

        if (
                !passwordEncoder.matches(
                        requestDTO.getCurrentPassword(),
                        user.getPassword()
                )
        ) {
            throw new BusinessException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);

        log.info(
                "Password changed successfully for user with id {}",
                user.getId()
        );
    }

    @Override
    public void resetPassword(String email) {
        log.info("Initializing password reset for email: {}", email);
        User user = findUserByEmailOrThrow(email);
        // TODO: Implement password reset functionality by sending email
        log.info("Password reset initialize for user: {}", user.getUsername());
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        log.info("Logout user");

        Claims accessTokenClaims = jwt.validateAndParseClaims(accessToken).getPayload();
        Claims refreshTokenClaims = jwt.validateAndParseClaims(refreshToken).getPayload();

        String username = accessTokenClaims.getSubject();
        User user = findUserByUsernameOrThrow(username);

        LocalDate accessTokenExpiry = jwt
                .getExpirationDateFromToken(accessToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate refreshTokenExpiry = jwt
                .getExpirationDateFromToken(refreshToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        BlackListToken accToken = BlackListToken.builder()
                .tokenId(accessTokenClaims.getId())
                .user(user)
                .expiryDate(accessTokenExpiry)
                .build();

        BlackListToken refToken = BlackListToken.builder()
                .tokenId(refreshTokenClaims.getId())
                .user(user)
                .expiryDate(refreshTokenExpiry)
                .build();

        blackListTokenRepository.save(accToken);
        blackListTokenRepository.save(refToken);

        log.info(
                "Access token has been blacklisted for user with id {}",
                user.getId()
        );
    }

    @Override
    public UserResponseDTO assignRole(Long userId, Long roleId) {
        log.info("Assign role: {} to user: {}", roleId, userId);

        User user = findUserByIdOrThrow(userId);
        Role role = findRoleByIdOrThrow(roleId);

        user.addRole(role);
        User updatedUser = userRepository.save(user);

        log.info("Assigned role successfully");
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public UserResponseDTO removeRole(Long userId, Long roleId) {
        log.info("Remove role: {} from user: {}", roleId, userId);

        User user = findUserByIdOrThrow(userId);
        Role role = findRoleByIdOrThrow(roleId);

        if (
                role.getName().equalsIgnoreCase("ROLE_ADMIN") &&
                        user.hasRole("ROLE_ADMIN")
        ) {
            long countAdmin = userRepository.countUserByRoleName("ROLE_ADMIN");
            if (countAdmin <= 1) {
                throw new BusinessException(
                        "Cannot remove ROLE_ADMIN role from last admin"
                );
            }
        }

        user.removeRole(role);
        User updatedUser = userRepository.save(user);

        log.info("Removed role successfully");
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public UserResponseDTO assignRoles(Long userId, List<Long> roleIds) {
        log.info("Assign multiple roles to user: {}", userId);

        User user = findUserByIdOrThrow(userId);
        List<Role> roles = roleRepository.findAllById(roleIds);

        roles.forEach(user::addRole);
        User updatedUser = userRepository.save(user);

        log.info("Assigned roles successfully");
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(String roleName) {
        log.debug("Fetching users with role {}", roleName);
        return userRepository
                .findAllByRoleName(roleName)
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void sendEmailVerification(Long userId) {
        log.info("Sending email verification for user ID: {}", userId);
        User user = findUserByIdOrThrow(userId);

        // TODO: Generate verification token and send email

        log.info("Email verification sent for user: {}", user.getUsername());
    }

    @Override
    public void verifyEmail(String token) {
        log.info("Verifying email with token");

        // TODO: Validate token and update user
        // This would typically involve:
        // 1. Validate token and check expiration
        // 2. Find user by token
        // 3. Mark email as verified

        log.info("Email verified successfully");
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + id + " not found"
                        )
                );
    }

    private User findUserByUsernameOrThrow(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username " + username + " not found"
                        )
                );
    }

    private User findUserByEmailOrThrow(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with email " + email + " not found"
                        )
                );
    }

    private Role findRoleByIdOrThrow(Long id) {
        return roleRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role with id " + id + " not found"
                        )
                );
    }
}
