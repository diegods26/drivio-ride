package com.drivio.passenger.domain;

import com.drivio.passenger.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneNumberTest {

    @Test
    void shouldAcceptCanonicalE164PhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber("+5511999999999");

        assertThat(phoneNumber.value()).isEqualTo("+5511999999999");
    }

    @Test
    void shouldRejectLocalNumberWithoutCountryCode() {
        assertThatThrownBy(() -> new PhoneNumber("11999999999"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectPhoneNumberWithWhitespaceOrLetters() {
        assertThatThrownBy(() -> new PhoneNumber("+55 11 99999-9999"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
