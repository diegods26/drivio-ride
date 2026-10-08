package com.drivio.driver.application.port.in;

import com.drivio.driver.application.dto.DriverProfileView;
import com.drivio.driver.application.dto.ManualDriverLicenseReviewCommand;

import java.util.UUID;

public interface ApproveDriverUseCase {

    DriverProfileView approve(UUID driverId);

    DriverProfileView reviewLicense(UUID driverId, ManualDriverLicenseReviewCommand command);
}