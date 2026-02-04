package com.s58703.demo.service;

import com.s58703.demo.dto.UserRequest;
import com.s58703.demo.dto.UserResponse;
import com.s58703.demo.entities.Role;
import com.s58703.demo.entities.User;
import com.s58703.demo.exception.ResourceNotFoundException;
import com.s58703.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        log.info("User created successfully with id: {}", saved.getId());

        return convertToResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(user);
        log.info("User updated successfully with id: {}", updated.getId());

        return convertToResponse(updated);
    }

    @Transactional
    public UserResponse patchUser(Long id, Map<String, Object> updates) {
        log.info("Patching user with id: {} with updates: {}", id, updates.keySet());

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "username":
                    String newUsername = (String) value;
                    if (!user.getUsername().equals(newUsername) &&
                            userRepository.findByUsername(newUsername).isPresent()) {
                        throw new IllegalArgumentException("Username already exists: " + newUsername);
                    }
                    user.setUsername(newUsername);
                    break;

                case "email":
                    String newEmail = (String) value;
                    if (!user.getEmail().equals(newEmail) &&
                            userRepository.findByEmail(newEmail).isPresent()) {
                        throw new IllegalArgumentException("Email already exists: " + newEmail);
                    }
                    user.setEmail(newEmail);
                    break;

                case "password":
                    String newPassword = (String) value;
                    if (newPassword != null && !newPassword.isEmpty()) {
                        user.setPassword(passwordEncoder.encode(newPassword));
                    }
                    break;

                case "role":
                    String roleName = (String) value;
                    try {
                        Role newRole = Role.valueOf(roleName.toUpperCase());
                        user.setRole(newRole);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid role: " + roleName);
                    }
                    break;

                default:
                    log.warn("Unknown field in patch request: {}", key);
                    throw new IllegalArgumentException("Unknown field: " + key);
            }
        });

        User patched = userRepository.save(user);

        log.info("User patched successfully with id: {}", patched.getId());
        return convertToResponse(patched);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
        log.info("User deleted successfully with id: {}", id);
    }

    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleName(user.getRole().name())
                .enabled(user.isEnabled())
                .build();
    }
}