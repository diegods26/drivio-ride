package com.drivio.iam.infrastructure.web.controller;

import com.drivio.bootstrap.security.SecurityConfig;
import com.drivio.iam.application.dto.RegisterUserCommand;
import com.drivio.iam.application.dto.RegisterUserResponse;
import com.drivio.iam.application.port.in.RegisterUserUseCase;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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


}
