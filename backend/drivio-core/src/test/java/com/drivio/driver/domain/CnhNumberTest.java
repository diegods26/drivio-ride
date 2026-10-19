package com.drivio.driver.domain;

import com.drivio.driver.domain.valueobject.CnhNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CnhNumberTest {

    @Test
    void shouldExposeNumberOnlyForExplicitVerificationAndRedactStringForm() {
        CnhNumber cnh = new CnhNumber("12345678901");

        assertThat(cnh.valueForVerification()).isEqualTo("12345678901");
        assertThat(cnh.toString()).doesNotContain("12345678901");
    }

    @Test
    void shouldRejectMalformedNumber() {
        assertThatThrownBy(() -> new CnhNumber("12345"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}