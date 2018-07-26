package com.convrt.api.controller;

import com.convrt.api.entity.Context;
import com.convrt.api.entity.User;
import com.convrt.api.service.ContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private ContextService contextService;

    @PostMapping("/register")
    public Context registerUser(@RequestHeader("User-Agent") String userAgent, @RequestBody User user) {
        return contextService.userRegister(user, userAgent);
    }

    @PostMapping("/login")
    public Context loginUser(@RequestHeader("email") String email, @RequestHeader("pin") String pin) {
        return contextService.userLogin(email, pin);
    }

}
