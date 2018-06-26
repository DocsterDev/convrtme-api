package com.convrt.controller;

import com.convrt.entity.Context;
import com.convrt.service.ContextService;
import com.convrt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class ContextController {

    @Autowired
    private UserService userService;
    @Autowired
    private ContextService contextService;

    @PostMapping("/register")
    public Context registerUser(@RequestHeader("email") String email, @RequestHeader("pin") String pin) {
        return contextService.userRegister(email, pin);
    }

    @PostMapping("/login")
    public Context loginUser(@RequestHeader("email") String email, @RequestHeader("pin") String pin) {
        return contextService.userLogin(email, pin);
    }

    // @RequestHeader(value = "token", required = false) String token <-- this goes everywhere auth is required

    @PostMapping("/authenticate")
    public Context authenticateUser(@RequestBody Context context){
        return contextService.authentication(context);
    }

    @PostMapping("/logout")
    public Context logoutUser(@RequestBody Context context){
        return contextService.userLogout(context);
    }

}
