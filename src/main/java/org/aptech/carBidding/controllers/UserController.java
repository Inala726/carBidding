package org.aptech.carBidding.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.aptech.carBidding.dtos.requests.ProfilePatchRequest;
import org.aptech.carBidding.dtos.requests.UserUpdateRequest;
import org.aptech.carBidding.dtos.response.UserDetailResponse;
import org.aptech.carBidding.dtos.response.UserListResponse;
import org.aptech.carBidding.services.UserServices;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userServices;

    /**
     * List all users. Only ADMINs can access.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        List<UserListResponse> users = userServices.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user details by ID. Only ADMINs can access.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long id) {
        UserDetailResponse user = userServices.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Update any user. Only ADMINs can access.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserDetailResponse updated = userServices.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a user account. Only ADMINs can access.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userServices.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get my own profile.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDetailResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails principal) {
        UserDetailResponse profile = userServices.getUserByEmail(principal.getUsername());
        return ResponseEntity.ok(profile);
    }

    /**
     * Patch my profile (firstName, lastName, profilePictureUrl).
     * Note: users cannot change their own status or timestamps here.
     */
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDetailResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ProfilePatchRequest request) {
        UserDetailResponse updated = userServices.updateUserProfile(
                principal.getUsername(), request);
        return ResponseEntity.ok(updated);
    }
}
