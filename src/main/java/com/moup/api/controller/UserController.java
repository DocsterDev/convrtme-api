package com.moup.api.controller;

import com.moup.api.entity.Context;
import com.moup.api.service.ContextService;
import com.moup.api.view.UserLocationWS;
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
    public Context registerUser(@RequestHeader("User-Agent") String userAgent, @RequestParam("email") String email, @RequestParam("pin") String pin, @RequestBody UserLocationWS userLocation) {
        return contextService.userRegister(email, pin, userAgent, userLocation);
    }

    @PostMapping("/authenticate")
    public Context authenticate(@RequestHeader("User-Agent") String userAgent, @RequestHeader(value = "token", required = false) String token, @RequestBody UserLocationWS userLocation){
        return contextService.userAuthenticate(token, userAgent, userLocation);
    }

    @PostMapping("/login")
    public Context loginUser(@RequestHeader("User-Agent") String userAgent, @RequestHeader("email") String email, @RequestHeader("pin") String pin, @RequestBody UserLocationWS userLocation) {
        return contextService.userLogin(email, pin, userAgent, userLocation);
    }

}
