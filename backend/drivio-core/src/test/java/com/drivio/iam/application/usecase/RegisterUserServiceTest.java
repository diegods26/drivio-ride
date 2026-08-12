package com.drivio.iam.application.usecase;

import com.drivio.iam.application.dto.RegisterUserCommand;
import com.drivio.iam.application.dto.RegisterUserResponse;
import com.drivio.iam.application.port.out.DomainEventPublisher;
import com.drivio.iam.application.port.out.PasswordEncoder;
import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.event.UserRegisteredEvent;
import com.drivio.iam.infrastructure.persistence.inmemory.InMemoryDomainEventPublisher;
import com.drivio.iam.infrastructure.persistence.inmemory.InMemoryUserRepository;
import com.drivio.iam.infrastructure.security.BCryptPasswordEncoderAdapter;
import com.drivio.iam.domain.valueobject.Email;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class RegisterUserServiceTest {

    @Test
    void shouldRegisterUserSuccessfully() {

        UserRepository repository = new InMemoryUserRepository();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoderAdapter();
        DomainEventPublisher eventPublisher = new InMemoryDomainEventPublisher();

        RegisterUserService service =
                new RegisterUserService(repository, passwordEncoder, eventPublisher);

        RegisterUserCommand command =
                new RegisterUserCommand(
                        "Diego Santos",
                        "diego@gmail.com",
                        "Password123!"
                );

        RegisterUserResponse response = service.register(command);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isNotNull();
        assertThat(repository.existsByEmail(
            new Email("diego@gmail.com")
        )).isTrue();

        InMemoryDomainEventPublisher publisher = (InMemoryDomainEventPublisher) eventPublisher;
        assertThat(publisher.events())
                .hasSize(1)
                .first()
                .isInstanceOf(UserRegisteredEvent.class);

    }
}
