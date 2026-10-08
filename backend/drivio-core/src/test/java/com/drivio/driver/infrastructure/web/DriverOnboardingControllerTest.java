package com.drivio.driver.infrastructure.web;

import com.drivio.bootstrap.security.SecurityConfig;
import com.drivio.driver.application.dto.DriverProfileView;
import com.drivio.driver.application.dto.DriverVehicleView;
import com.drivio.driver.application.port.in.ApproveDriverUseCase;
import com.drivio.driver.application.port.in.ApproveDriverVehicleUseCase;
import com.drivio.driver.application.port.in.ManageDriverOnboardingUseCase;
import com.drivio.driver.domain.model.DriverLicenseStatus;
import com.drivio.driver.domain.model.DriverStatus;
import com.drivio.driver.infrastructure.web.controller.AdminDriverController;
import com.drivio.driver.infrastructure.web.controller.DriverOnboardingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({DriverOnboardingController.class, AdminDriverController.class})
@Import({SecurityConfig.class, DriverExceptionHandler.class})
class DriverOnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManageDriverOnboardingUseCase manageDriverOnboardingUseCase;

    @MockitoBean
    private ApproveDriverUseCase approveDriverUseCase;

        @MockitoBean
        private ApproveDriverVehicleUseCase approveDriverVehicleUseCase;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldRequireAuthenticationForDriverOnboarding() throws Exception {
        mockMvc.perform(get("/api/v1/drivers/me"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(manageDriverOnboardingUseCase);
    }

    @Test
    void shouldRejectMalformedLicenseBeforeCallingApplication() throws Exception {
        mockMvc.perform(post("/api/v1/drivers/me/license-verification")
                        .with(user("driver@example.com").roles("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licenseNumber\":\"1234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(manageDriverOnboardingUseCase);
    }

    @Test
    void shouldPassCnhForVerificationWithoutReturningIt() throws Exception {
        UUID driverId = UUID.randomUUID();
        when(manageDriverOnboardingUseCase.verifyMyLicense(any())).thenReturn(
                new DriverProfileView(
                        driverId,
                        DriverStatus.ONBOARDING,
                        DriverLicenseStatus.UNAVAILABLE,
                        null,
                        false,
                        false
                )
        );

        mockMvc.perform(post("/api/v1/drivers/me/license-verification")
                        .with(user("driver@example.com").roles("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"licenseNumber\":\"12345678901\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licenseStatus").value("UNAVAILABLE"))
                .andExpect(jsonPath("$.online").value(false))
                .andExpect(jsonPath("$.licenseNumber").doesNotExist());

        verify(manageDriverOnboardingUseCase).verifyMyLicense(any());
    }

    @Test
    void shouldRequireAdminRoleToApproveDriver() throws Exception {
        mockMvc.perform(post("/api/v1/admin/drivers/{driverId}/approval", UUID.randomUUID())
                        .with(user("driver@example.com").roles("DRIVER")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(approveDriverUseCase);
    }

    @Test
    void shouldAllowAdminApprovalEndpoint() throws Exception {
        UUID driverId = UUID.randomUUID();
        when(approveDriverUseCase.approve(driverId)).thenReturn(
                new DriverProfileView(
                        driverId,
                        DriverStatus.APPROVED,
                        DriverLicenseStatus.VERIFIED,
                        LocalDate.now().plusYears(1),
                        false,
                        true
                )
        );

        mockMvc.perform(post("/api/v1/admin/drivers/{driverId}/approval", driverId)
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(true));

        verify(approveDriverUseCase).approve(driverId);
    }

    @Test
    void shouldAllowAdminToApproveVehicleAndReturnActiveState() throws Exception {
        UUID vehicleId = UUID.randomUUID();
        when(approveDriverVehicleUseCase.approve(vehicleId)).thenReturn(
                new DriverVehicleView(
                        vehicleId,
                        "ABC1D23",
                        "Toyota",
                        "Corolla",
                        LocalDate.now().getYear(),
                        com.drivio.driver.domain.model.VehicleCategory.STANDARD,
                        true
                )
        );

        mockMvc.perform(post("/api/v1/admin/drivers/vehicles/{vehicleId}/approval", vehicleId)
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        verify(approveDriverVehicleUseCase).approve(vehicleId);
    }
}
