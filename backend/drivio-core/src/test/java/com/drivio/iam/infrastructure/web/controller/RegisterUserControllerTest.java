package com.drivio.iam.infrastructure.web.controller;

import com.drivio.bootstrap.security.SecurityConfig;
import com.drivio.iam.application.dto.RegisterUserCommand;
import com.drivio.iam.application.dto.RegisterUserResponse;
import com.drivio.iam.application.port.in.RegisterUserUseCase;
import com.drivio.iam.domain.exception.EmailAlreadyExistsException;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.infrastructure.web.dto.RegisterUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisterUserController.class)
@Import(SecurityConfig.class)
public class RegisterUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        RegisterUserRequest request =
                new RegisterUserRequest(
                        "Diego Santos",
                        "diego@gmail.com",
                        "12345678909",
                        "Password123!"
                );

        RegisterUserResponse response = new RegisterUserResponse(userId);

        when(registerUserUseCase.register(any(RegisterUserCommand.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(registerUserUseCase).register(any(RegisterUserCommand.class));
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "Diego Santos",
                "diego@gmail.com",
                "12345678909",
                "Password123!"
        );

        when(registerUserUseCase.register(any(RegisterUserCommand.class))
                ).thenThrow(new EmailAlreadyExistsException(
                        new Email("diego@gmail.com"))
        );

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"))
        .andExpect(jsonPath("$.message").value("Email already exists"))
        .andExpect(jsonPath("$.timestamp").exists());

        verify(registerUserUseCase).register(any(RegisterUserCommand.class));

    }

    @Test
    void shouldRejectBlankFullName() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "",
                "diego@gmail.com",
                "12345678909",
                "Password123!"
        );

        mockMvc.perform(
                post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldRejectInvalidEmailFormat() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "Diego Santos",
                "invalid_email",
                "12345678909",
                "Password123!"
        );

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldRejectShortPassword() throws  Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "Diego Santos",
                "diego@gmail.com",
                "12345678909",
                "123!"
        );

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(registerUserUseCase);
    }

}
