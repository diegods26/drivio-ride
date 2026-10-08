package com.drivio.iam.application.port.in;

public interface AuthenticateUserUseCase {

    AuthenticateUserResponse authenticate(AuthenticateUserCommand command);
}
