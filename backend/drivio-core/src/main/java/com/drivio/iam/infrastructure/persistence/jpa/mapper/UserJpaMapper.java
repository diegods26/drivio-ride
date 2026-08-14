package com.drivio.iam.infrastructure.persistence.jpa.mapper;

import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.FullName;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.domain.valueobject.UserId;
import com.drivio.iam.infrastructure.persistence.jpa.entity.UserJpaEntity;

public final class UserJpaMapper {

    private UserJpaMapper() {}

    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.id().value(),
                user.fullName().value(),
                user.email().value(),
                user.hashedPassword().value(),
                user.role(),
                user.isActive(),
                user.createdAt(),
                user.updatedAt()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
        return User.restore(
                new UserId(entity.getId()),
                new FullName(entity.getFullName()),
                new Email(entity.getEmail()),
                new HashedPassword(entity.getHashedPassword()),
                entity.getRole(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
