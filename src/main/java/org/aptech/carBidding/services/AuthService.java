package org.aptech.carBidding.services;

import org.aptech.carBidding.dtos.requests.LoginRequest;
import org.aptech.carBidding.dtos.requests.RegisterRequest;
import org.aptech.carBidding.dtos.response.LoginResponse;
import org.aptech.carBidding.dtos.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
    /**
     * Initiate a password reset by sending a one-time code
     * to the user’s email (if it exists).
     */
//    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    /**
     * Complete a password reset by validating the code
     * and setting the new password.
     */
//    ResetPasswordResponse resetPassword(ResetPasswordRequest request);

}
