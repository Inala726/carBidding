package org.aptech.carBidding.services;

import org.aptech.carBidding.dtos.requests.ProfilePatchRequest;
import org.aptech.carBidding.dtos.requests.UserUpdateRequest;
import org.aptech.carBidding.dtos.response.UserDetailResponse;
import org.aptech.carBidding.dtos.response.UserListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserServices {

    List<UserListResponse> getAllUsers();
    UserDetailResponse getUserById(Long id);
    UserDetailResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);
    UserDetailResponse getUserByEmail(String email);
    UserDetailResponse updateUserProfile(String email, ProfilePatchRequest request);
    String updateProfilePicture(String email, MultipartFile file);
}

