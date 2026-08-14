package com.drivio.iam.infrastructure.persistence.jpa.mapper;

import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.domain.valueobject.FullName;
import com.drivio.iam.domain.valueobject.HashedPassword;
import com.drivio.iam.infrastructure.persistence.jpa.entity.UserJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class UserJpaMapperTest {

    @Test
    void shouldMapDomainUserToJpaEntity() {
        User user = User.create(
                new FullName("John Doe"),
                new Email("john@gmail.com"),
                new HashedPassword("hashedPassword123")
        );

        UserJpaEntity entity = UserJpaMapper.toEntity(user);

        assertThat(entity.getId()).isEqualTo(user.id().value());
        assertThat(entity.getFullName()).isEqualTo(user.fullName().value());
        assertThat(entity.getEmail()).isEqualTo(user.email().value());
        assertThat(entity.getHashedPassword()).isEqualTo(user.hashedPassword().value());
        assertThat(entity.getRole()).isEqualTo(user.role());
        assertThat(entity.isActive()).isEqualTo(user.isActive());
        assertThat(entity.getCreatedAt()).isEqualTo(user.createdAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(user.updatedAt());
    }

    @Test
    void shouldMapJpaEntityToDomainUser() {

        User originalUser = User.create(
                new FullName("Jane Doe"),
                new Email("jane@gmail.com"),
                new HashedPassword("hashedPassword456")
        );

        UserJpaEntity entity = UserJpaMapper.toEntity(originalUser);

        User restoredUser = UserJpaMapper.toDomain(entity);

        assertThat(restoredUser.id()).isEqualTo(originalUser.id());
        assertThat(restoredUser.fullName()).isEqualTo(originalUser.fullName());
        assertThat(restoredUser.email()).isEqualTo(originalUser.email());
        assertThat(restoredUser.hashedPassword()).isEqualTo(originalUser.hashedPassword());
        assertThat(restoredUser.role()).isEqualTo(originalUser.role());
        assertThat(restoredUser.isActive()).isEqualTo(originalUser.isActive());
        assertThat(restoredUser.createdAt()).isEqualTo(originalUser.createdAt());
        assertThat(restoredUser.updatedAt()).isEqualTo(originalUser.updatedAt());
    }
}
