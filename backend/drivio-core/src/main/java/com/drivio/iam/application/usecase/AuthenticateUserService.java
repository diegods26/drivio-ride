package com.drivio.iam.application.usecase;

import com.drivio.iam.application.port.in.AuthenticateUserCommand;
import com.drivio.iam.application.port.in.AuthenticateUserResponse;
import com.drivio.iam.application.port.in.AuthenticateUserUseCase;
import com.drivio.iam.application.port.out.PasswordVerifier;
import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.exception.InvalidCredentialsException;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.RawPassword;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;

    public AuthenticateUserService(UserRepository userRepository, PasswordVerifier passwordVerifier) {
        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
    }

    @Override
    public AuthenticateUserResponse authenticate(AuthenticateUserCommand command) {
        Email email = new Email(command.email());
        RawPassword rawPassword = new RawPassword(command.password());

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        boolean passwordMatches =
                passwordVerifier.matches(rawPassword, user.hashedPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return new AuthenticateUserResponse(
                user.id().value(),
                user.email().value(),
                user.role().name()
        );
    }
}
