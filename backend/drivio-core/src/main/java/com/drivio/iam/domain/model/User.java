package com.drivio.iam.domain.model;

import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.FullName;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.UserId;

import java.time.Instant;

public class User {

    private final UserId id;
    private FullName fullName;
    private Email email;
    private HashedPassword hashedPassword;
    private Role role;
    private boolean isActive;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(UserId id,
                 FullName fullName,
                 Email email,
                 HashedPassword hashedPassword,
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

    public static User create(FullName fullName,
                              Email email,
                              HashedPassword hashedPassword) {
        UserId id = UserId.generate();
        Instant now = Instant.now();
        return new User(
                id,
                fullName,
                email,
                hashedPassword,
                Role.PASSENGER,
                true,
                now,
                now
        );
    }

    public static User restore(UserId id,
                               FullName fullName,
                               Email email,
                               HashedPassword hashedPassword,
                               Role role,
                               boolean isActive,
                               Instant createdAt,
                               Instant updatedAt
    ) {
        return new User(
                id,
                fullName,
                email,
                hashedPassword,
                role,
                isActive,
                createdAt,
                updatedAt
        );
    }

    public UserId id() {
        return id;
    }

    public FullName fullName() {
        return fullName;
    }

    public Email email() {
        return email;
    }

    public HashedPassword hashedPassword() {
        return hashedPassword;
    }

    public Role role() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
