package com.s58703.demo.controllers;

import com.s58703.demo.entities.User;
import com.s58703.demo.service.UserService;
import com.s58703.demo.dto.UserRequest;
import com.s58703.demo.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Enhanced User Controller
 * Implements all HTTP methods: GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD
 *
 * Features:
 * - Full CRUD operations
 * - Partial updates (PATCH)
 * - Metadata queries (HEAD, OPTIONS)
 * - Content negotiation support
 * - Proper status codes
 * - Role-based access control
 * - Self-data access only (users can only access their own data)
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ========== GET Methods ==========

    /**
     * GET all users (ADMIN only)
     * Returns: 200 OK with list of users
     */
    @GetMapping(produces = {"application/json", "application/xml"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .header("Cache-Control", "private, max-age=300")
                .body(users);
    }

    /**
     * GET current authenticated user's profile
     * Returns: 200 OK with user data
     */
    @GetMapping(value = "/me", produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        UserResponse userResponse = userService.getUserById(user.getId());

        return ResponseEntity.ok()
                .header("Cache-Control", "private, max-age=60")
                .body(userResponse);
    }

    /**
     * GET user by ID
     * Returns: 200 OK if found and authorized, 403 Forbidden if not own data, 404 Not Found if not exists
     */
    @GetMapping(value = "/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        // Users can only access their own data unless they're admin
        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse userResponse = userService.getUserById(id);

        return ResponseEntity.ok()
                .header("Cache-Control", "private, max-age=60")
                .body(userResponse);
    }

    // ========== POST Method ==========

    /**
     * POST create new user (ADMIN only - regular users register via /api/auth/register)
     * Returns: 201 Created with Location header
     */
    @PostMapping(consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/users/" + created.getId())
                .body(created);
    }

    // ========== PUT Method (Full Update) ==========

    /**
     * PUT update entire user profile (idempotent)
     * Users can only update their own data
     * Returns: 200 OK with updated resource
     */
    @PutMapping(value = "/{id}",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request,
            @AuthenticationPrincipal User user) {

        // Users can only update their own data unless they're admin
        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse updated = userService.updateUser(id, request);

        return ResponseEntity.ok(updated);
    }

    /**
     * PUT update current user's profile
     * Returns: 200 OK with updated resource
     */
    @PutMapping(value = "/me",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> updateCurrentUser(
            @Valid @RequestBody UserRequest request,
            @AuthenticationPrincipal User user) {

        UserResponse updated = userService.updateUser(user.getId(), request);

        return ResponseEntity.ok(updated);
    }

    // ========== PATCH Method (Partial Update) ==========

    /**
     * PATCH partially update user profile
     * Allows updating individual fields without sending entire resource
     * Users can only update their own data
     * Returns: 200 OK with updated resource
     *
     * Example:
     * PATCH /api/users/1
     * {"email": "newemail@example.com"}
     */
    @PatchMapping(value = "/{id}",
            consumes = {"application/json"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal User user) {

        // Users can only update their own data unless they're admin
        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse patched = userService.patchUser(id, updates);

        return ResponseEntity.ok(patched);
    }

    /**
     * PATCH partially update current user's profile
     * Returns: 200 OK with updated resource
     */
    @PatchMapping(value = "/me",
            consumes = {"application/json"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> patchCurrentUser(
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal User user) {

        UserResponse patched = userService.patchUser(user.getId(), updates);

        return ResponseEntity.ok(patched);
    }

    // ========== DELETE Method ==========

    /**
     * DELETE remove user (idempotent)
     * Users can only delete their own account unless they're admin
     * Returns: 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        // Users can only delete their own account unless they're admin
        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE current user's account
     * Returns: 204 No Content
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    // ========== OPTIONS Method ==========

    /**
     * OPTIONS get allowed methods for this endpoint
     * Used for CORS preflight requests
     * Returns: 200 OK with Allow header
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok()
                .allow(
                        HttpMethod.GET,
                        HttpMethod.POST,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE,
                        HttpMethod.OPTIONS,
                        HttpMethod.HEAD
                )
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    /**
     * OPTIONS for specific resource
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> optionsById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .allow(
                        HttpMethod.GET,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE,
                        HttpMethod.OPTIONS,
                        HttpMethod.HEAD
                )
                .header("Access-Control-Allow-Methods", "GET, PUT, PATCH, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    /**
     * OPTIONS for /me endpoint
     */
    @RequestMapping(value = "/me", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> optionsMe() {
        return ResponseEntity.ok()
                .allow(
                        HttpMethod.GET,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE,
                        HttpMethod.OPTIONS,
                        HttpMethod.HEAD
                )
                .header("Access-Control-Allow-Methods", "GET, PUT, PATCH, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    // ========== HEAD Method ==========

    /**
     * HEAD get metadata without response body
     * Useful for checking if resource exists or getting headers
     * Returns: Same headers as GET but without body
     */
    @RequestMapping(method = RequestMethod.HEAD)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> headAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .header("Content-Type", "application/json")
                .header("Cache-Control", "private, max-age=300")
                .build();
    }

    /**
     * HEAD for current user
     */
    @RequestMapping(value = "/me", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Cache-Control", "private, max-age=60")
                .build();
    }

    /**
     * HEAD for specific resource
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        // Users can only access their own data unless they're admin
        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.getUserById(id);

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Cache-Control", "private, max-age=60")
                .build();
    }
}