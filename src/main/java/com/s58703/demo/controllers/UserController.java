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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(produces = {"application/json", "application/xml"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .header("Cache-Control", "private, max-age=300")
                .body(users);
    }

    @GetMapping(value = "/me", produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        UserResponse userResponse = userService.getUserById(user.getId());

        return ResponseEntity.ok()
                .header("Cache-Control", "private, max-age=60")
                .body(userResponse);
    }

    @GetMapping(value = "/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse userResponse = userService.getUserById(id);

        return ResponseEntity.ok()
                .header("Cache-Control", "private, max-age=60")
                .body(userResponse);
    }

    @PostMapping(consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/users/" + created.getId())
                .body(created);
    }

    @PutMapping(value = "/{id}",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request,
            @AuthenticationPrincipal User user) {

        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse updated = userService.updateUser(id, request);

        return ResponseEntity.ok(updated);
    }

    @PutMapping(value = "/me",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> updateCurrentUser(
            @Valid @RequestBody UserRequest request,
            @AuthenticationPrincipal User user) {

        UserResponse updated = userService.updateUser(user.getId(), request);

        return ResponseEntity.ok(updated);
    }

    @PatchMapping(value = "/{id}",
            consumes = {"application/json"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal User user) {

        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse patched = userService.patchUser(id, updates);

        return ResponseEntity.ok(patched);
    }

    @PatchMapping(value = "/me",
            consumes = {"application/json"},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<UserResponse> patchCurrentUser(
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal User user) {

        UserResponse patched = userService.patchUser(user.getId(), updates);

        return ResponseEntity.ok(patched);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (!user.getId().equals(id) && user.getRole().name().equals("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

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

    @RequestMapping(value = "/me", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Cache-Control", "private, max-age=60")
                .build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

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