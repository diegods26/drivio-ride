package com.drivio.iam.application.usecase;

import com.drivio.iam.application.dto.RegisterUserCommand;
import com.drivio.iam.application.dto.RegisterUserResponse;
import com.drivio.iam.application.port.in.RegisterUserUseCase;
import com.drivio.iam.application.port.out.DomainEventPublisher;
import com.drivio.iam.application.port.out.PasswordEncoder;
import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.event.UserRegisteredEvent;
import com.drivio.iam.domain.exception.CpfAlreadyExistsException;
import com.drivio.iam.domain.exception.EmailAlreadyExistsException;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Cpf;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.FullName;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.RawPassword;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    public RegisterUserService(UserRepository repository,
                               PasswordEncoder passwordEncoder,
                               DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public RegisterUserResponse register(RegisterUserCommand command) {
        return register(command, Role.PASSENGER);
    }

    @Override
    public RegisterUserResponse registerDriver(RegisterUserCommand command) {
        return register(command, Role.DRIVER);
    }

    private RegisterUserResponse register(
            RegisterUserCommand command,
            Role role
    ) {
        Email email = new Email(command.email());
        FullName fullName = new FullName(command.fullName());
        Cpf cpf = new Cpf(command.cpf());

        RawPassword rawPassword = new RawPassword(command.password());

        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        if (repository.existsByCpf(cpf)) {
            throw new CpfAlreadyExistsException();
        }

        HashedPassword hashedPassword =
                passwordEncoder.encode(rawPassword);

        User user = switch (role) {
            case PASSENGER -> User.create(fullName, email, cpf, hashedPassword);
            case DRIVER -> User.createDriver(fullName, email, cpf, hashedPassword);
            case ADMIN -> throw new IllegalArgumentException(
                "Administrative users cannot self-register"
            );
        };

        repository.save(user);

        eventPublisher.publish(new UserRegisteredEvent(user.id()));

        return new RegisterUserResponse(user.id().value());
    }
}
