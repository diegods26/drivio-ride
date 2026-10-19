package com.drivio.driver.infrastructure.security;

import com.drivio.driver.application.port.out.DriverLicenseVerifier;
import com.drivio.driver.domain.model.LicenseVerificationResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DriverLicenseVerifierConfiguration {

    @Bean
    @ConditionalOnMissingBean(DriverLicenseVerifier.class)
    DriverLicenseVerifier unavailableDriverLicenseVerifier() {
        return (cpf, licenseNumber) ->
                LicenseVerificationResult.unavailable("AUTHORIZED_PROVIDER_NOT_CONFIGURED");
    }
}