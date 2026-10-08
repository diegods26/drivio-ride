package com.drivio.driver.application.port.in;

import com.drivio.driver.application.dto.DriverAvailabilityCommand;
import com.drivio.driver.application.dto.DriverProfileView;
import com.drivio.driver.application.dto.DriverVehicleView;
import com.drivio.driver.application.dto.RegisterDriverVehicleCommand;
import com.drivio.driver.application.dto.VerifyDriverLicenseCommand;

import java.util.List;

public interface ManageDriverOnboardingUseCase {

    DriverProfileView getMyProfile();

    DriverProfileView verifyMyLicense(VerifyDriverLicenseCommand command);

    DriverVehicleView registerMyVehicle(RegisterDriverVehicleCommand command);

    List<DriverVehicleView> listMyVehicles();

    DriverProfileView changeAvailability(DriverAvailabilityCommand command);
}