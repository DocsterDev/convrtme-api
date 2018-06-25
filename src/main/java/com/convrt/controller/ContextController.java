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
        return contextService.userRegister(email, pin, ctxLog, userAgent);
    }

    @PostMapping("/login")
    public Context loginUser(@RequestHeader("email") String email, @RequestHeader("pin") String pin, @RequestHeader(value = "User-Agent", required = false) String userAgent, @RequestBody(required = false) Log ctxLog) {
        return contextService.userLogin(email, pin, ctxLog, userAgent);
    }

    // @RequestHeader(value = "token", required = false) String token <-- this goes everywhere auth is required

    @PostMapping("/logout")
    public void logoutUser(@RequestHeader("token") String token, @RequestBody(required = false) Log ctxLog){
        contextService.userLogout(token, ctxLog);
    }

}
