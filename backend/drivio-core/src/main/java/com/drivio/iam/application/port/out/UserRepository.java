package com.drivio.iam.application.port.out;

import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;

public interface UserRepository {

    boolean existsByEmail(Email email);

    User save(User user);
}
