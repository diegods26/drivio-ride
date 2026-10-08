package com.drivio.iam.security;

import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.*;
import com.drivio.iam.infrastructure.security.SpringUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SpringUserDetailsServiceTest {

    private UserRepository userRepository;
    private SpringUserDetailsService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new SpringUserDetailsService(userRepository);
    }

    @Test
    void shouldLoadActiveUserByEmail() {
        User user = createUser(true);

        when(userRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("diego@gmail.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername())
                .isEqualTo("diego@gmail.com");

        assertThat(result.getPassword())
                .isEqualTo("hashed-password");

        assertThat(result.isEnabled())
                .isTrue();

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_PASSENGER");

        verify(userRepository)
                .findByEmail(any(Email.class));
    }

    @Test
    void shouldRejectUnknownUser() {

        when(userRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.loadUserByUsername("unknown@gmail.com")
        )
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldDisableInactiveUser() {

        User user = createUser(false);

        when(userRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("diego@gmail.com");

        assertThat(result.isEnabled())
                .isFalse();
    }

    private User createUser(boolean active) {

        return User.restore(
                UserId.generate(),
                new FullName("Diego Santos"),
                new Email("diego@gmail.com"),
                new Cpf("41134746059"),
                new HashedPassword("hashed-password"),
                Role.PASSENGER,
                active,
                Instant.now(),
                Instant.now()
        );
    }
}
