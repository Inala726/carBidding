package org.aptech.carBidding.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Role entity for car-bidding system.
 * Examples: ROLE_ADMIN, ROLE_SELLER, ROLE_BIDDER
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** e.g. "ROLE_ADMIN", "ROLE_SELLER", "ROLE_BIDDER" */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Users assigned to this role.
     * Mapped by the 'roles' field on the User entity.
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

}
