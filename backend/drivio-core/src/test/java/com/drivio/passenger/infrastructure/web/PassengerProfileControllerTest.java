package com.drivio.passenger.infrastructure.web;

import com.drivio.bootstrap.security.SecurityConfig;
import com.drivio.passenger.application.dto.PassengerProfileView;
import com.drivio.passenger.application.port.in.ManagePassengerProfileUseCase;
import com.drivio.passenger.domain.exception.PassengerAccessDeniedException;
import com.drivio.passenger.infrastructure.web.controller.PassengerProfileController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PassengerProfileController.class)
@Import({SecurityConfig.class, PassengerExceptionHandler.class})
class PassengerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManagePassengerProfileUseCase managePassengerProfileUseCase;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldReturnAuthenticatedPassengerProfile() throws Exception {
        UUID passengerId = UUID.randomUUID();
        when(managePassengerProfileUseCase.getMyProfile()).thenReturn(
                new PassengerProfileView(
                        passengerId, "Diego Santos", "diego@example.com", null
                )
        );

        mockMvc.perform(get("/api/v1/passengers/me")
                        .with(user("diego@example.com").roles("PASSENGER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(passengerId.toString()))
                .andExpect(jsonPath("$.email").value("diego@example.com"));

        verify(managePassengerProfileUseCase).getMyProfile();
    }

        @Test
        void shouldRequireAuthenticationForPassengerProfile() throws Exception {
                mockMvc.perform(get("/api/v1/passengers/me"))
                                .andExpect(status().isUnauthorized());

                verifyNoInteractions(managePassengerProfileUseCase);
        }

        @Test
        void shouldReturnForbiddenWhenCurrentIdentityIsNotPassenger() throws Exception {
                when(managePassengerProfileUseCase.getMyProfile())
                                .thenThrow(new PassengerAccessDeniedException());

                mockMvc.perform(get("/api/v1/passengers/me")
                                                .with(user("driver@example.com").roles("DRIVER")))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.code").value("PASSENGER_ACCESS_DENIED"));
        }

    @Test
    void shouldRejectInvalidPhoneAtHttpBoundary() throws Exception {
        String request = """
                {"phoneNumber":"11999999999"}
                """;

        mockMvc.perform(put("/api/v1/passengers/me")
                        .with(user("diego@example.com").roles("PASSENGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(managePassengerProfileUseCase);
    }

    @Test
    void shouldForwardPhoneUpdateWithoutAcceptingAnOwnerId() throws Exception {
        UUID passengerId = UUID.randomUUID();
        when(managePassengerProfileUseCase.updateMyProfile(any()))
                .thenReturn(new PassengerProfileView(
                        passengerId,
                        "Diego Santos",
                        "diego@example.com",
                        "+5511999999999"
                ));

        mockMvc.perform(put("/api/v1/passengers/me")
                        .with(user("diego@example.com").roles("PASSENGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"+5511999999999\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("+5511999999999"));

        verify(managePassengerProfileUseCase).updateMyProfile(any());
    }
}