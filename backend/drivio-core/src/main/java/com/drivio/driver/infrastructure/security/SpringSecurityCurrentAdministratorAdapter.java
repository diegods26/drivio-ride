package com.drivio.driver.infrastructure.security;

import com.drivio.driver.application.port.out.CurrentAdministratorProvider;
import com.drivio.driver.domain.exception.AdministratorAccessDeniedException;
import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.exception.InvalidEmailException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SpringSecurityCurrentAdministratorAdapter implements CurrentAdministratorProvider {

    private final UserRepository userRepository;

    public SpringSecurityCurrentAdministratorAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UUID getCurrentAdministratorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AdministratorAccessDeniedException();
        }

        Email email;
        try {
            email = new Email(authentication.getName());
        } catch (InvalidEmailException exception) {
            throw new AdministratorAccessDeniedException();
        }

        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .filter(candidate -> candidate.role() == Role.ADMIN)
                .orElseThrow(AdministratorAccessDeniedException::new);
        return user.id().value();
    }
}