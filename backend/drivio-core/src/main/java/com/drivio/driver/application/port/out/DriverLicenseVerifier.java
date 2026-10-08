package com.drivio.driver.application.port.out;

import com.drivio.driver.domain.model.LicenseVerificationResult;
import com.drivio.driver.domain.valueobject.CnhNumber;
import com.drivio.iam.domain.valueobject.Cpf;

public interface DriverLicenseVerifier {

    LicenseVerificationResult verify(Cpf cpf, CnhNumber licenseNumber);
}