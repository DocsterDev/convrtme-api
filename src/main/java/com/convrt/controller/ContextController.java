package com.convrt.controller;

import com.convrt.entity.Context;
import com.convrt.entity.User;
import com.convrt.service.ContextService;
import com.convrt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/context")
public class ContextController {

    /*

    Call "http://www.geoplugin.net/json.gp" to get region and ip info

     */

    @Autowired
    private UserService userService;
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

    // @RequestHeader(value = "token", required = false) String token <-- this goes everywhere auth is required

    @PostMapping("/authenticate")
    public Context authenticateUser(@RequestHeader("User-Agent") String userAgent, @RequestHeader("token") String token){
        return contextService.validateContext(token, userAgent);
    }

    @PostMapping("/logout")
    public Context logoutUser(@RequestBody Context context){
        return contextService.userLogout(context);
    }

}
