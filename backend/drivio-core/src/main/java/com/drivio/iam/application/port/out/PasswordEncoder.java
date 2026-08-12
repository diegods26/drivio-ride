package com.drivio.iam.application.port.out;

import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.RawPassword;

public interface PasswordEncoder {

    HashedPassword encode(RawPassword rawPassword);
}
