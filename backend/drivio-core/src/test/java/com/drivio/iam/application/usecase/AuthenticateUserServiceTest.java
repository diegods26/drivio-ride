package com.drivio.iam.application.usecase;

import com.drivio.iam.application.port.in.AuthenticateUserCommand;
import com.drivio.iam.application.port.in.AuthenticateUserResponse;
import com.drivio.iam.application.port.out.PasswordVerifier;
import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.exception.InvalidCredentialsException;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Cpf;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.FullName;
import com.drivio.iam.domain.valueobject.HashedPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticateUserServiceTest {

    private UserRepository userRepository;
    private PasswordVerifier passwordVerifier;
    private AuthenticateUserService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordVerifier = mock(PasswordVerifier.class);

        service = new AuthenticateUserService(
                userRepository,
                passwordVerifier
        );
    }

    @Test
    void shouldAuthenticateUserWithValidCredentials() {

        User user = User.create(
                new FullName("Diego Pimenta"),
                new Email("diego@example.com"),
                new Cpf("17930139000"),
                new HashedPassword("hashed-password")
        );

        when(userRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(user));

        when(passwordVerifier.matches(
                any(),
                any()
        )).thenReturn(true);

        AuthenticateUserResponse response =
                service.authenticate(
                        new AuthenticateUserCommand(
                                "diego@example.com",
                                "correct-password"
                        )
                );

        assertThat(response.userId())
                .isEqualTo(user.id().value());

        assertThat(response.email())
                .isEqualTo("diego@example.com");

        assertThat(response.role())
                .isEqualTo("PASSENGER");

        verify(passwordVerifier)
                .matches(any(), any());
    }

    @Test
    void shouldRejectUnknownUser() {

        when(userRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.authenticate(
                        new AuthenticateUserCommand(
                                "unknown@example.com",
                                "password"
                        )
                )
        )
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(passwordVerifier);
    }

    @Test
    void shouldRejectInvalidPassword() {

        User user = User.create(
                new FullName("Diego Pimenta"),
                new Email("diego@example.com"),
                new Cpf("17930139000"),
                new HashedPassword("hashed-password")
        );

        when(userRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(user));

        when(passwordVerifier.matches(
                any(),
                any()
        )).thenReturn(false);

        assertThatThrownBy(() ->
                service.authenticate(
                        new AuthenticateUserCommand(
                                "diego@example.com",
                                "wrong-password"
                        )
                )
        )
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void shouldRejectInactiveUser() {

        User activeUser = User.create(
                new FullName("Diego Pimenta"),
                new Email("diego@example.com"),
                new Cpf("17930139000"),
                new HashedPassword("hashed-password")
        );

        User inactiveUser = User.restore(
                activeUser.id(),
                activeUser.fullName(),
                activeUser.email(),
                activeUser.cpf(),
                activeUser.hashedPassword(),
                activeUser.role(),
                false,
                activeUser.createdAt(),
                activeUser.updatedAt()
        );

        when(userRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() ->
                service.authenticate(
                        new AuthenticateUserCommand(
                                "diego@example.com",
                                "correct-password"
                        )
                )
        )
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(passwordVerifier);
    }
}
