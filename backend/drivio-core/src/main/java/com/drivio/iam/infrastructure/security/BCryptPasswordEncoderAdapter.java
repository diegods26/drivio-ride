package com.drivio.iam.infrastructure.security;

import com.drivio.iam.application.port.out.PasswordEncoder;
import com.drivio.iam.application.port.out.PasswordVerifier;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.RawPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder, PasswordVerifier {

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    @Override
    public HashedPassword encode(RawPassword rawPassword) {
        String hash = encoder.encode(rawPassword.value());
        return new HashedPassword(hash);
    }

    @Override
    public boolean matches(RawPassword rawPassword, HashedPassword hashedPassword) {
        return encoder.matches(rawPassword.value(), hashedPassword.value());
    }
}
