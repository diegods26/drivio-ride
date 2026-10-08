package com.drivio.driver.infrastructure.security;

import com.drivio.driver.domain.model.DriverLicenseStatus;
import com.drivio.driver.domain.valueobject.CnhNumber;
import com.drivio.iam.domain.valueobject.Cpf;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DriverLicenseVerifierConfigurationTest {

    @Test
    void shouldFailClosedWhenAuthorizedVerificationProviderIsNotConfigured() {
        var verifier = new DriverLicenseVerifierConfiguration()
                .unavailableDriverLicenseVerifier();

        var result = verifier.verify(
                new Cpf("41134746059"),
                new CnhNumber("12345678901")
        );

        assertThat(result.status()).isEqualTo(DriverLicenseStatus.UNAVAILABLE);
        assertThat(result.providerCode()).isEqualTo("AUTHORIZED_PROVIDER_NOT_CONFIGURED");
        assertThat(result.auditReference()).isNull();
    }
}
