package com.drivio.iam.application.port.out;

import com.drivio.iam.domain.event.DomainEvent;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
