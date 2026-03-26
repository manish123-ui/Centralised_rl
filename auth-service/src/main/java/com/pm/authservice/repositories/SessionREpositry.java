package com.pm.authservice.repositories;

import com.pm.authservice.entity.Session;
import com.pm.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionREpositry  extends JpaRepository<Session, Long> {
    List<Session> findByUser(User user);

    Optional<Session> findByRefreshToken(String refreshToken);

}
