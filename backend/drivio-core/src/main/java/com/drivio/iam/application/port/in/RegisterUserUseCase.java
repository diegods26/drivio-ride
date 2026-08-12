package com.drivio.iam.application.port.in;

import com.drivio.iam.application.dto.RegisterUserCommand;
import com.drivio.iam.application.dto.RegisterUserResponse;

public interface RegisterUserUseCase {
    RegisterUserResponse register(RegisterUserCommand command);
}
