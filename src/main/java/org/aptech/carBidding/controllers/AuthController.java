package org.aptech.carBidding.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.aptech.carBidding.dtos.requests.LoginRequest;
import org.aptech.carBidding.dtos.requests.RegisterRequest;
import org.aptech.carBidding.dtos.response.LoginResponse;
import org.aptech.carBidding.dtos.response.RegisterResponse;
import org.aptech.carBidding.services.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Sign up as seller or bidder.
     * POST body must include role (e.g. "ROLE_SELLER" or "ROLE_BIDDER").
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Log in with email + password, receive a JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // removed forgot/reset-password endpoints, since AuthService no longer supports them
}

