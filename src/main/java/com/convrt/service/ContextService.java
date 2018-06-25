package com.convrt.service;

import com.convrt.entity.Context;
import com.convrt.entity.Log;
import com.convrt.entity.User;
import com.convrt.enums.ActionType;
import com.convrt.repository.ContextRepository;
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
public class ContextService {

    @Autowired
    private ContextRepository contextRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public Context userRegister(String email, String pin, Log ctxLog, String userAgent) {
        ContextService.log.info("Registering user with email {}", email);
        ctxLog.setUuid(UUID.randomUUID().toString());
        ctxLog.setDateAccessed(Instant.now());
        ctxLog.setAction(ActionType.REGISTER);
        User user = userService.createUser(email, pin);
        String token = RandomStringUtils.randomAlphanumeric(100);
        Context context = new Context();
        context.setUuid(UUID.randomUUID().toString());
        context.setToken(token);
        context.setUserAgent(userAgent);
        context.setUser(user);
        context.setValid(true);
        context.setLastLogin(Instant.now());
        context.addLog(ctxLog);
        return contextRepository.save(context);
    }

    @Transactional
    public Context userLogin(String email, String pin, Log ctxLog, String userAgent) {
        log.info("Logging in user with email {}", email);
        User user = userService.getUserByPinAndEmail(pin, email);
        ctxLog.setUuid(UUID.randomUUID().toString());
        ctxLog.setDateAccessed(Instant.now());
        ctxLog.setAction(ActionType.LOGIN);
        Context contextPersistent = contextRepository.findByUserAndUserAgentAndValidIsTrue(user, userAgent);
        if (contextPersistent != null) {
            log.warn("User with email {} on user agent {} is already logged in. Creating new context...", email, userAgent);
            contextPersistent.setValid(false);
            // TODO MAKE SURE THIS IS SET TO FALSE IN THE DB
        }
        String token = RandomStringUtils.randomAlphanumeric(100);
        Context context = new Context();
        context.setUuid(UUID.randomUUID().toString());
        context.setToken(token);
        context.setUserAgent(userAgent);
        context.setUser(user);
        context.setValid(true);
        context.setLastLogin(Instant.now());
        context.addLog(ctxLog);
        return contextRepository.save(context);
    }

    @Transactional(readOnly = true)
    public Context readContext(String token, String userAgent) {
        log.info("Looking up existing user session for token {}", token);
        return contextRepository.findByTokenAndValidIsTrue(token);
    }

    @Transactional(readOnly = true)
    public Context validateContext(String token, String userAgent) {
        log.info("Validating token {}", token);
        Context context = contextRepository.findByTokenAndUserAgentAndValidIsTrue(token, userAgent);
        if(context == null) {
            throw new RuntimeException("No user context found");
        }
        return context;
    }

    @Transactional(readOnly = true)
    public Context validateContext(String token) {
        log.info("Validating token {}", token);
        Context context = contextRepository.findByTokenAndValidIsTrue(token);
        if(context == null) {
            throw new RuntimeException("No user context found");
        }
        return context;
    }

    @Transactional
    public void userLogout(String token, Log ctxLog) {
        ctxLog.setUuid(UUID.randomUUID().toString());
        ctxLog.setDateAccessed(Instant.now());
        ctxLog.setAction(ActionType.LOGOUT);
        Context context = contextRepository.findByTokenAndValidIsTrue(token);
        if(context == null) {
            throw new RuntimeException("No valid context found to logout for token " + token);
        }
        context.addLog(ctxLog);
        context.setValid(false);
        // TODO make sure this was set to false
        log.info("MAKE SURE THIS WAS SET TO FALSE");
    }


}
