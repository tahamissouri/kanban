package com.example.persistence.repository;

import com.example.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserName(String username);
    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}
