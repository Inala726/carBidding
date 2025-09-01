package org.aptech.carBidding.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    /**
     * Uploads the given file to Cloudinary and returns the secure URL.
     */
    String uploadImage(MultipartFile file);

}