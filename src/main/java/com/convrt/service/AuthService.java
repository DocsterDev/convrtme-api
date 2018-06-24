package com.convrt.service;

import com.convrt.entity.Auth;
import com.convrt.entity.User;
import com.convrt.repository.AuthRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Transactional
    public Auth generateUserToken(String userAgent, User user) {
        log.info("Generating token for user {}", user.getEmail());
        Auth authPersistent = authRepository.findByUserAgentAndUserUuid(userAgent, user.getUuid());
        if (authPersistent != null) {
            return authPersistent;
        }
        String token = RandomStringUtils.randomAlphanumeric(100);
        Auth auth = new Auth();
        auth.setToken(token);
        auth.setUser(user);
        auth.setValid(true);
        auth.setLastLogin(Instant.now());
        auth.setUserAgent(userAgent);
        auth.setUuid(UUID.randomUUID().toString());
        return authRepository.save(auth);
    }

    @Transactional(readOnly = true)
    public Auth lookupUserToken(User user, String userAgent) {
        log.info("Looking up existing user session for user {}", user.getEmail());
        Auth auth = authRepository.findByUserAndUserAgent(user, userAgent);
        if (auth == null) {
            log.error("User {} already logged in", user.getEmail());
            return auth;
        }
        return null;
    }


}