package com.drivio.driver.infrastructure.security;

import com.drivio.driver.application.dto.CurrentDriver;
import com.drivio.driver.application.port.out.CurrentDriverProvider;
import com.drivio.driver.domain.exception.DriverAccessDeniedException;
import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.exception.InvalidEmailException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityCurrentDriverAdapter implements CurrentDriverProvider {

    private final UserRepository userRepository;

    public SpringSecurityCurrentDriverAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CurrentDriver getCurrentDriver() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new DriverAccessDeniedException();
        }

        Email email;
        try {
            email = new Email(authentication.getName());
        } catch (InvalidEmailException exception) {
            throw new DriverAccessDeniedException();
        }

        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .filter(candidate -> candidate.role() == Role.DRIVER)
                .orElseThrow(DriverAccessDeniedException::new);
        return new CurrentDriver(user.id().value(), user.cpf());
    }
}