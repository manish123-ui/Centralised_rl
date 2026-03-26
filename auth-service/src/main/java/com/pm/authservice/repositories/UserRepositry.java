package com.pm.authservice.repositories;

import com.pm.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositry extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
