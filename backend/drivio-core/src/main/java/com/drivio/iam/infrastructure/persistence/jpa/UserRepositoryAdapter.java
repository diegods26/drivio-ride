package com.drivio.iam.infrastructure.persistence.jpa;

import com.drivio.iam.application.port.out.UserRepository;
import com.drivio.iam.domain.model.User;
import com.drivio.iam.domain.valueobject.Cpf;
import com.drivio.iam.domain.valueobject.Email;
import com.drivio.iam.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.drivio.iam.infrastructure.persistence.jpa.mapper.UserJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;

    public UserRepositoryAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }

    @Override
    public boolean existsByCpf(Cpf cpf) {
        return repository.existsByCpf(cpf.value());
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository
                .findByEmail(email.value())
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaMapper.toEntity(user);
        UserJpaEntity savedEntity = repository.save(entity);

        return UserJpaMapper.toDomain(savedEntity);
    }
}
