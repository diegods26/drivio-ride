package com.drivio.iam.infrastructure.web.controller;

import com.drivio.iam.application.dto.RegisterUserCommand;
import com.drivio.iam.application.dto.RegisterUserResponse;
import com.drivio.iam.application.port.in.RegisterUserUseCase;
import com.drivio.iam.infrastructure.web.dto.RegisterUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class RegisterUserController {

    private final RegisterUserUseCase registerUserUseCase;

    public RegisterUserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    public ResponseEntity<RegisterUserResponse>
        register(@Valid @RequestBody RegisterUserRequest request)
    {
        RegisterUserCommand command = new RegisterUserCommand(
                request.fullname(),
                request.email(),
                request.cpf(),
                request.password()
        );

        RegisterUserResponse response = registerUserUseCase.register(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
