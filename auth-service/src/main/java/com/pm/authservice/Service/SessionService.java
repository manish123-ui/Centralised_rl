package com.pm.authservice.Service;


import com.pm.authservice.entity.Session;
import com.pm.authservice.entity.User;
import com.pm.authservice.repositories.SessionREpositry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionREpositry sessionRepository;
    private final int SESSION_LIMIT = 2;

    public void generateNewSession(User user, String refreshToken){
        List<Session> userSessions = sessionRepository.findByUser(user);
        if(userSessions.size() >= SESSION_LIMIT){
            userSessions.sort(Comparator.comparing(Session::getLastUsedAt));
            Session leastRecentlyUsedSession = userSessions.getFirst();
            sessionRepository.delete(leastRecentlyUsedSession);
        }
        Session newSession = Session
                .builder()
                .user(user)
                .refreshToken(refreshToken)
                .build();
        sessionRepository.save(newSession);
    }
    public void validateSession(String token){
        Session session =sessionRepository.findByRefreshToken(token).orElseThrow(() -> new SessionAuthenticationException("Session not found for this refresh token may be deleted becaiuse of k session"+token));
        session.setLastUsedAt(LocalDateTime.now());
    }




}
