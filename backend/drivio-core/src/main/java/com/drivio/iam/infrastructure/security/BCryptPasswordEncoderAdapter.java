package com.drivio.iam.infrastructure.security;

import com.drivio.iam.application.port.out.PasswordEncoder;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.RawPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    @Override
    public HashedPassword encode(RawPassword rawPassword) {
        String hash = encoder.encode(rawPassword.value());
        return new HashedPassword(hash);
    }
}
