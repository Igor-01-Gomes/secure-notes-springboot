package com.igor.securenotes.repository;

import com.igor.securenotes.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}