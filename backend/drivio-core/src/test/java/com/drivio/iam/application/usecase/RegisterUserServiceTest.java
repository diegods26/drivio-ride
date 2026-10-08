package com.drivio.iam.application.usecase;

import com.drivio.iam.application.dto.RegisterUserCommand;
import com.drivio.iam.application.dto.RegisterUserResponse;
import com.drivio.iam.application.port.out.PasswordEncoder;
import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.event.UserRegisteredEvent;
import com.drivio.iam.domain.exception.CpfAlreadyExistsException;
import com.drivio.iam.domain.exception.EmailAlreadyExistsException;
import com.drivio.iam.domain.exception.InvalidCpfException;
import com.drivio.iam.domain.exception.InvalidEmailException;
import com.drivio.iam.domain.exception.InvalidPasswordException;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.infrastructure.persistence.inmemory.InMemoryDomainEventPublisher;
import com.drivio.iam.infrastructure.persistence.inmemory.InMemoryUserRepository;
import com.drivio.iam.infrastructure.security.BCryptPasswordEncoderAdapter;
import com.drivio.iam.domain.valueobject.Email;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterUserServiceTest {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private InMemoryDomainEventPublisher eventPublisher;
    private RegisterUserService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        passwordEncoder = new BCryptPasswordEncoderAdapter();
        eventPublisher = new InMemoryDomainEventPublisher();
        service = new RegisterUserService(repository, passwordEncoder, eventPublisher);
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterUserCommand command =
                new RegisterUserCommand(
                        "Diego Santos",
                        "diego@gmail.com",
                        "12345678909",
                        "Password123!"
                );

        RegisterUserResponse response = service.register(command);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isNotNull();
        assertThat(repository.existsByEmail(
            new Email("diego@gmail.com")
        )).isTrue();
        assertThat(repository.findByEmail(new Email("diego@gmail.com")))
                .get()
                .extracting("role")
                .isEqualTo(Role.PASSENGER);

        assertThat(eventPublisher.events())
                .hasSize(1)
                .first()
                .isInstanceOf(UserRegisteredEvent.class);

    }

    @Test
    void shouldRegisterDriverWithDriverRole() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Morgan Driver",
                "driver@gmail.com",
                "41134746059",
                "Password123!"
        );

        service.registerDriver(command);

        assertThat(repository.findByEmail(new Email("driver@gmail.com")))
                .get()
                .extracting("role")
                .isEqualTo(Role.DRIVER);
    }

    @Test
    void shouldRejectInvalidCpfWithoutPersistingOrPublishingEvent() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Morgan Driver",
                "driver@gmail.com",
                "41134746050",
                "Password123!"
        );

        assertThatThrownBy(() -> service.registerDriver(command))
                .isInstanceOf(InvalidCpfException.class);

        assertThat(repository.findByEmail(new Email("driver@gmail.com")))
                .isEmpty();
        assertThat(eventPublisher.events()).isEmpty();
    }

    @Test
    void shouldRejectCpfAlreadyRegistered() {
        RegisterUserCommand first = new RegisterUserCommand(
                "Morgan Passenger",
                "passenger@gmail.com",
                "41134746059",
                "Password123!"
        );
        RegisterUserCommand second = new RegisterUserCommand(
                "Morgan Driver",
                "driver@gmail.com",
                "41134746059",
                "Password123!"
        );

        service.register(first);

        assertThatThrownBy(() -> service.registerDriver(second))
                .isInstanceOf(CpfAlreadyExistsException.class);

        assertThat(repository.findByEmail(new Email("driver@gmail.com")))
                .isEmpty();
        assertThat(eventPublisher.events()).hasSize(1);
    }

    @Test
    void shouldRejectDuplicatedEmail() {

        RegisterUserCommand firstCommand =
                new RegisterUserCommand(
                        "Diego Santos",
                        "diego@gmail.com",
                        "12345678909",
                        "Password123!"
                );

        service.register(firstCommand);

        RegisterUserCommand secondCommand =
                new RegisterUserCommand(
                        "John Doe",
                        "diego@gmail.com",
                        "98765432100",
                        "Password456!"
                );

        assertThatThrownBy(() -> service.register(secondCommand))
                .isInstanceOf(EmailAlreadyExistsException.class);

        assertThat(eventPublisher.events())
                .hasSize(1);
    }

    @Test
    void shouldRejectInvalidPassword() {

        RegisterUserCommand command =
                new RegisterUserCommand(
                        "Diego Santos",
                        "diego@gmail.com",
                        "12345678909",
                        "123"
                );

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(InvalidPasswordException.class);

        assertThat(repository.existsByEmail(
            new Email("diego@gmail.com")
        )).isFalse();

        assertThat(eventPublisher.events()).isEmpty();
    }

    @Test
    void shouldRejectInvalidEmail() {

        RegisterUserCommand command =
                new RegisterUserCommand(
                        "Diego Santos",
                        "invalid-email",
                        "12345678909",
                        "Password123!"
                );

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(InvalidEmailException.class);

        assertThat(eventPublisher.events()).isEmpty();
    }
}
