package com.drivio.passenger.infrastructure.security;

import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.exception.InvalidEmailException;
import com.drivio.passenger.application.dto.PassengerIdentity;
import com.drivio.passenger.application.port.out.CurrentPassengerProvider;
import com.drivio.passenger.domain.exception.PassengerAccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityCurrentPassengerAdapter implements CurrentPassengerProvider {

    private final UserRepository userRepository;

    public SpringSecurityCurrentPassengerAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public PassengerIdentity getCurrentPassenger() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new PassengerAccessDeniedException();
        }

        Email email;
        try {
            email = new Email(authentication.getName());
        } catch (InvalidEmailException exception) {
            throw new PassengerAccessDeniedException();
        }

        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .filter(candidate -> candidate.role() == Role.PASSENGER)
                .orElseThrow(PassengerAccessDeniedException::new);

        return new PassengerIdentity(
                user.id().value(),
                user.fullName().value(),
                user.email().value()
        );
    }
}