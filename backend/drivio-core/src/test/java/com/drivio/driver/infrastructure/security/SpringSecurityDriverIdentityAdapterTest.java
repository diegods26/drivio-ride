package com.drivio.driver.infrastructure.security;

import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Cpf;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.FullName;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.UserId;
import com.drivio.passenger.domain.exception.PassengerAccessDeniedException;
import com.drivio.driver.domain.exception.DriverAccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringSecurityDriverIdentityAdapterTest {

    private UserRepository userRepository;
    private SpringSecurityCurrentDriverAdapter driverAdapter;
    private SpringSecurityCurrentAdministratorAdapter administratorAdapter;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        driverAdapter = new SpringSecurityCurrentDriverAdapter(userRepository);
        administratorAdapter = new SpringSecurityCurrentAdministratorAdapter(userRepository);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldResolveDriverIdentityAndRedactCpfInStringForm() {
        UUID driverId = UUID.randomUUID();
        authenticate("driver@example.com");
        when(userRepository.findByEmail(new Email("driver@example.com")))
                .thenReturn(Optional.of(user(driverId, Role.DRIVER, "driver@example.com", true)));

        var currentDriver = driverAdapter.getCurrentDriver();

        assertThat(currentDriver.id()).isEqualTo(driverId);
        assertThat(currentDriver.cpf().value()).isEqualTo("41134746059");
        assertThat(currentDriver.toString()).doesNotContain("41134746059");
    }

    @Test
    void shouldRejectPassengerFromDriverOperations() {
        authenticate("passenger@example.com");
        when(userRepository.findByEmail(new Email("passenger@example.com")))
                .thenReturn(Optional.of(user(
                        UUID.randomUUID(), Role.PASSENGER, "passenger@example.com", true
                )));

        assertThatThrownBy(driverAdapter::getCurrentDriver)
                .isInstanceOf(DriverAccessDeniedException.class);
    }

    @Test
    void shouldResolveOnlyActiveAdministrator() {
        UUID administratorId = UUID.randomUUID();
        authenticate("admin@example.com");
        when(userRepository.findByEmail(new Email("admin@example.com")))
                .thenReturn(Optional.of(user(
                        administratorId, Role.ADMIN, "admin@example.com", true
                )));

        assertThat(administratorAdapter.getCurrentAdministratorId()).isEqualTo(administratorId);
    }

    @Test
    void shouldRejectDriverAsAdministrator() {
        authenticate("driver@example.com");
        when(userRepository.findByEmail(new Email("driver@example.com")))
                .thenReturn(Optional.of(user(
                        UUID.randomUUID(), Role.DRIVER, "driver@example.com", true
                )));

        assertThatThrownBy(administratorAdapter::getCurrentAdministratorId)
                .isInstanceOf(com.drivio.driver.domain.exception.AdministratorAccessDeniedException.class);
    }

    private static void authenticate(String subject) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(subject, "token", List.of())
        );
    }

    private static User user(UUID id, Role role, String email, boolean active) {
        Instant now = Instant.now();
        return User.restore(
                new UserId(id),
                new FullName("Test Account"),
                new Email(email),
                new Cpf("41134746059"),
                new HashedPassword("encoded-password"),
                role,
                active,
                now,
                now
        );
    }
}
