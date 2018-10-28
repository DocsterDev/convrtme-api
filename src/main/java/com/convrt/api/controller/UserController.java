package com.convrt.api.controller;

import com.convrt.api.entity.Context;
import com.convrt.api.entity.User;
import com.convrt.api.service.ContextService;
import com.convrt.api.view.UserLocationWS;
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
    public Context loginUser(@RequestHeader("User-Agent") String userAgent, @RequestParam("email") String email, @RequestParam("pin") String pin, @RequestBody UserLocationWS userLocation) {
        return contextService.userLogin(email, pin, userAgent, userLocation);
    }

}
