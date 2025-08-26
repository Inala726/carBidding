package org.aptech.carBidding.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cars")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Car {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;
    private int year;
    private String description;

    @Column(nullable = true)  // Allow null if no image is uploaded (optional)
    private String imageUrl;  // New field for Cloudinary URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
