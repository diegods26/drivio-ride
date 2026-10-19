package com.drivio.iam.security;

import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.RawPassword;
import com.drivio.iam.infrastructure.security.BCryptPasswordEncoderAdapter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BCryptPasswordEncoderAdapterTest {

    private final BCryptPasswordEncoderAdapter adapter =
            new BCryptPasswordEncoderAdapter();

    @Test
    void shouldMatchCorrectPassword() {

        RawPassword rawPassword =
                new RawPassword("correct-password");

        HashedPassword hashedPassword =
                adapter.encode(rawPassword);

        assertThat(
                adapter.matches(rawPassword, hashedPassword)
        ).isTrue();
    }

    @Test
    void shouldRejectIncorrectPassword() {

        HashedPassword hashedPassword =
                adapter.encode(
                        new RawPassword("correct-password")
                );

        assertThat(
                adapter.matches(
                        new RawPassword("wrong-password"),
                        hashedPassword
                )
        ).isFalse();
    }
}
