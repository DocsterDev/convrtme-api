package com.convrt.controller;

import com.convrt.entity.Context;
import com.convrt.entity.Log;
import com.convrt.entity.User;
import com.convrt.enums.ActionType;
import com.convrt.service.ContextService;
import com.convrt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class ContextController {

    @Autowired
    private UserService userService;
    @Autowired
    private ContextService contextService;

    @PostMapping("/register")
    public Context registerUser(@RequestHeader("email") String email, @RequestHeader("pin") String pin, @RequestHeader(value = "User-Agent", required = false) String userAgent, @RequestBody(required = false) Log ctxLog) {
        User user = userService.createUser(email, pin);
        ctxLog.setUuid(UUID.randomUUID().toString());
        ctxLog.setDateAccessed(Instant.now());
        ctxLog.setAction(ActionType.REGISTER);
        return contextService.createContext(user, ctxLog, userAgent);
    }

    // @RequestHeader(value = "token", required = false) String token <-- this goes everywhere auth is required

    @PostMapping("/login")
    public Context loginUser(@RequestHeader("email") String email, @RequestHeader("pin") String pin, @RequestHeader(value = "User-Agent", required = false) String userAgent, @RequestBody(required = false) Log ctxLog) {
        User user = userService.getUserByPinAndEmail(pin, email);
        ctxLog.setUuid(UUID.randomUUID().toString());
        ctxLog.setDateAccessed(Instant.now());
        ctxLog.setAction(ActionType.LOGIN);
        return contextService.createContext(user, ctxLog, userAgent);
    }

    @PostMapping("/logout")
    public void logoutUser(@RequestHeader("token") String token, @RequestHeader(value = "User-Agent", required = false) String userAgent, @RequestBody(required = false) Log ctxLog){
        ctxLog.setUuid(UUID.randomUUID().toString());
        ctxLog.setDateAccessed(Instant.now());
        ctxLog.setAction(ActionType.LOGOUT);
        contextService.invalidateContext(token, ctxLog, userAgent);
    }

}
