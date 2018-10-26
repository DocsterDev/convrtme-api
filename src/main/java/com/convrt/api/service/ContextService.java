package com.convrt.api.service;

import com.convrt.api.entity.Context;
import com.convrt.api.entity.User;
import com.convrt.api.repository.ContextRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
public class ContextService {

    private static final String ANONYMOUS_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    @Autowired
    private ContextRepository contextRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public Context userRegister(User user, String userAgent) {
        log.info("Registering user with email {}", user.getEmail());
        User userPersist = userService.createUser(user);
        Context context = new Context();
        context.setToken(RandomStringUtils.randomAlphanumeric(100));
        context.setUser(userPersist);
        context.setUserAgent(userAgent);
        context.setValid(true);
        context.setLastLogin(Instant.now());
        return contextRepository.save(context);
    }

    @Transactional
    public Context userLogin(String email, String pin) {
        log.info("Logging in user with email {}", email);
        User user = userService.getUserByPinAndEmail(pin, email);
        Context contextPersistent = contextRepository.findByUserAndValidIsTrue(user);
        if (contextPersistent != null) {
            throw new RuntimeException(String.format("User email %s is already logged in", email));
        }
        String token = RandomStringUtils.randomAlphanumeric(100);
        Context context = new Context();
        context.setToken(token);
        context.setUser(user);
        context.setValid(true);
        context.setLastLogin(Instant.now());
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
        if (context == null) {
            context = createAnonymousContext(userAgent);
        }
        return context;
    }

    private Context createAnonymousContext(String userAgent) {
        log.warn("User context not found. Generating new context.");
        User user = new User();
        user.setEmail(String.format("%s@moup.io", UUID.randomUUID().toString()));
        user.setPin("1234");
        return userRegister(user, userAgent);
    }

    @Transactional(readOnly = true)
    public User validateAndGetUser(String token){
        Context context = validateContext(token);
        if (context == null) {
            context = createAnonymousContext(ANONYMOUS_USER_AGENT);
        }
        User user = context.getUser();
        if (user == null) {
            throw new RuntimeException("Cannot find user to add subscription.");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Context validateContext(String token) {
        log.info("Validating token {}", token);
        if(token == null) {
            throw new RuntimeException("Cannot validate token. Token is null.");
        }
        return contextRepository.findByTokenAndValidIsTrue(token);
    }

    @Transactional
    public Context userLogout(Context context) {
        context = contextRepository.findByTokenAndValidIsTrue(context.getToken());
        if(context == null) {
            throw new RuntimeException(String.format("No valid context found to logout for token %s", context.getToken()));
        }
        context.setValid(false);
        // TODO make sure this was set to false
        log.info("MAKE SURE THIS WAS SET TO FALSE");
        return new Context();
    }


}
