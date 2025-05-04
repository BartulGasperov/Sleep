package com.noom.interview.fullstack.sleep.persistence.repository;

import com.noom.interview.fullstack.sleep.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUuid(UUID uuid);
}
