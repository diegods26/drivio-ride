package com.drivio.iam.application.usecase;

import com.drivio.iam.application.dto.RegisterUserCommand;
import com.drivio.iam.application.dto.RegisterUserResponse;
import com.drivio.iam.application.port.in.RegisterUserUseCase;
import com.drivio.iam.application.port.out.DomainEventPublisher;
import com.drivio.iam.application.port.out.PasswordEncoder;
import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.event.UserRegisteredEvent;
import com.drivio.iam.domain.exception.EmailAlreadyExistsException;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.FullName;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.RawPassword;


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
        Email email = new Email(command.email());
        FullName fullName = new FullName(command.fullName());

        RawPassword rawPassword = new RawPassword(command.password());

        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        HashedPassword hashedPassword =
                passwordEncoder.encode(rawPassword);

        User user = User.create(fullName, email, hashedPassword);

        repository.save(user);

        eventPublisher.publish(new UserRegisteredEvent(user.id()));

        return new RegisterUserResponse(user.id().value());
    }
}
