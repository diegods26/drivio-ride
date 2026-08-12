package com.drivio.iam.infrastructure.persistence.inmemory;

import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public boolean existsByEmail(Email email) {
        return users.values().stream()
                .anyMatch(user -> user.email().equals(email));
    }

    @Override
    public User save(User user) {
        users.put(user.id().value(), user);
        return user;
    }

}
