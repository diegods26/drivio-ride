package com.drivio.iam.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.drivio.iam.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    void shouldNormalizeEmailToLowerCase() {
        Email email = new Email("Diego.Pimenta@Email.com");
        assertThat(email.value()).isEqualTo("diego.pimenta@email.com");
    }

    @Test
    void shouldTrimWhitespace() {
        Email email = new Email("   diego@email.com   ");
        assertThat(email.value()).isEqualTo("diego@email.com");
    }

    @Test
    void shouldRejectNullEmail() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void shouldRejectBlankEmail() {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        assertThatThrownBy(() -> new Email("abc"))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void shouldConsiderEqualEmailsIgnoringCase() {
        Email firstEmail = new Email("Diego@email.com");
        Email secondEmail = new Email("diego@email.com");
        assertThat(firstEmail).isEqualTo(secondEmail);
    }
}
