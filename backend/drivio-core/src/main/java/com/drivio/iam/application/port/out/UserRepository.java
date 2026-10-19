package com.drivio.iam.application.port.out;

import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Cpf;
import com.drivio.iam.domain.valueobject.Email;

import java.util.Optional;

public interface UserRepository {

    boolean existsByEmail(Email email);

    boolean existsByCpf(Cpf cpf);

    Optional<User> findByEmail(Email email);

    User save(User user);
}
