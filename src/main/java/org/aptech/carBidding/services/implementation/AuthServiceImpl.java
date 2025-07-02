package org.aptech.carBidding.services.implementation;

import lombok.RequiredArgsConstructor;
import org.aptech.carBidding.dtos.requests.RegisterRequest;
import org.aptech.carBidding.dtos.response.RegisterResponse;
import org.aptech.carBidding.enums.UserStatus;
import org.aptech.carBidding.models.Role;
import org.aptech.carBidding.models.User;
import org.aptech.carBidding.repository.RoleRepository;
import org.aptech.carBidding.repository.UserRepository;
import org.aptech.carBidding.security.jwt.JwtTokenProvider;
import org.aptech.carBidding.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.aptech.carBidding.dtos.requests.LoginRequest;
import org.aptech.carBidding.dtos.response.LoginResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // 1. Prevent duplicate emails
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException(
                    "A user with email '" + request.getEmail() + "' already exists."
            );
        }

        String requestedRoleName = request.getRole();
        Role chosenRole = roleRepository.findByName(requestedRoleName)
                .orElseThrow(() -> new IllegalStateException(
                        "Requested role " + requestedRoleName + " not found in database."
                ));

        // 3. Build and save user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(UserStatus.ACTIVE)        // if you re-added status
                .createdAt(LocalDateTime.now())  // if you re-added timestamps
                .updatedAt(LocalDateTime.now())
                .roles(Collections.singleton(chosenRole))
                .build();

        user = userRepository.save(user);

        // 4. Return DTO
        return RegisterResponse.builder()
                .userId(user.getId())
                .message("Registration successful for user: " + user.getEmail())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. Authenticate credentials
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Set context and generate JWT
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtTokenProvider.generateToken(auth);

        // 3. Return token payload
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getValidityInMilliseconds())
                .build();
    }
}