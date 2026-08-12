package com.drivio.iam.infrastructure.persistence.inmemory;

import com.drivio.iam.application.port.out.DomainEventPublisher;
import com.drivio.iam.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryDomainEventPublisher implements DomainEventPublisher {

    private final List<DomainEvent> events = new ArrayList<>();

    @Override
    public void publish(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> events() {
        return Collections.unmodifiableList(events);
    }
}
