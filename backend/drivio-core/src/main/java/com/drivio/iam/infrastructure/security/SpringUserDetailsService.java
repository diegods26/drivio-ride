package com.drivio.iam.infrastructure.security;

import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SpringUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SpringUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Email email = new Email(username);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.email().value())
                .password(user.hashedPassword().value())
                .disabled(!user.isActive())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.role().name()))
                .build();
    }
}
