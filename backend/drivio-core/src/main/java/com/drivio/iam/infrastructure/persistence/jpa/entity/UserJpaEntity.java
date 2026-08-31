package com.drivio.iam.infrastructure.persistence.jpa.entity;

import com.drivio.iam.domain.model.Role;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserJpaEntity() { }

    public UserJpaEntity(UUID id,
                         String fullName,
                         String email,
                         String hashedPassword,
                         Role role,
                         boolean isActive,
                         Instant createdAt,
                         Instant updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
