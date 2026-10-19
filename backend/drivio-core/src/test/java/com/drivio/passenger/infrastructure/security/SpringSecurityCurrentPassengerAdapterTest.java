package com.drivio.passenger.infrastructure.security;

import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Cpf;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.FullName;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.UserId;
import com.drivio.passenger.domain.exception.PassengerAccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringSecurityCurrentPassengerAdapterTest {

    private UserRepository userRepository;
    private SpringSecurityCurrentPassengerAdapter adapter;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        adapter = new SpringSecurityCurrentPassengerAdapter(userRepository);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldResolveOnlyActivePassengerFromAuthenticatedSubject() {
        UUID userId = UUID.randomUUID();
        authenticate("passenger@example.com");
        when(userRepository.findByEmail(new Email("passenger@example.com")))
                .thenReturn(Optional.of(user(userId, Role.PASSENGER, true)));

        var passenger = adapter.getCurrentPassenger();

        assertThat(passenger.id()).isEqualTo(userId);
        assertThat(passenger.email()).isEqualTo("passenger@example.com");
    }

    @Test
    void shouldRejectDriverEvenWhenTheTokenIsAuthenticated() {
        authenticate("driver@example.com");
        when(userRepository.findByEmail(new Email("driver@example.com")))
                .thenReturn(Optional.of(user(UUID.randomUUID(), Role.DRIVER, true)));

        assertThatThrownBy(adapter::getCurrentPassenger)
                .isInstanceOf(PassengerAccessDeniedException.class);
    }

    @Test
    void shouldRejectInactivePassenger() {
        authenticate("passenger@example.com");
        when(userRepository.findByEmail(new Email("passenger@example.com")))
                .thenReturn(Optional.of(user(UUID.randomUUID(), Role.PASSENGER, false)));

        assertThatThrownBy(adapter::getCurrentPassenger)
                .isInstanceOf(PassengerAccessDeniedException.class);
    }

    private static void authenticate(String subject) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(subject, "token", java.util.List.of())
        );
    }

    private static User user(UUID id, Role role, boolean active) {
        Instant now = Instant.now();
        return User.restore(
                new UserId(id),
                new FullName("Test Passenger"),
                new Email(role == Role.DRIVER ? "driver@example.com" : "passenger@example.com"),
                new Cpf("41134746059"),
                new HashedPassword("encoded-password"),
                role,
                active,
                now,
                now
        );
    }
}