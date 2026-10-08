package com.drivio.iam.domain.valueobject;

import com.drivio.iam.domain.exception.InvalidCpfException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CpfTest {

    @Test
    void shouldNormalizeFormattedCpf() {
        Cpf cpf = new Cpf("411.347.460-59");

        assertThat(cpf.value()).isEqualTo("41134746059");
    }

    @Test
    void shouldRejectCpfWithInvalidCheckDigits() {
        assertThatThrownBy(() -> new Cpf("41134746050"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldRejectNonCpfCharactersInsteadOfDiscardingThem() {
        assertThatThrownBy(() -> new Cpf("abc411.347.460-59"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldNotExposeCpfInValidationExceptionMessage() {
        assertThatThrownBy(() -> new Cpf("41134746050"))
                .isInstanceOf(InvalidCpfException.class)
                .hasMessage("Invalid CPF");
    }
}
