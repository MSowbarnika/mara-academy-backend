package com.maraacademy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maraacademy.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Login uses this, so an email saved twice by mistake never causes a server error
    List<User> findAllByEmail(String email);

    Optional<User> findByToken(String token);

    boolean existsByEmail(String email);
}
