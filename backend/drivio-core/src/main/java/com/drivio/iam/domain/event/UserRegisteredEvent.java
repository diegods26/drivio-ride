package com.drivio.iam.domain.event;

import com.drivio.iam.domain.valueobject.UserId;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        Instant occurredAt,
        UserId userId
) implements DomainEvent {

    public  UserRegisteredEvent(UserId userId) {
        this(UUID.randomUUID(), Instant.now(), userId);
    }
}
