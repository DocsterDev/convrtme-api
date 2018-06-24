package com.convrt.controller;

import com.convrt.entity.Auth;
import com.convrt.entity.User;
import com.convrt.service.AuthService;
import com.convrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    // TODO Delete after development is done
    @GetMapping
    public List<User> getUsers() {
        return userService.readUsers();
    }

    // TODO Delete after development is done
    @GetMapping("/{uuid}")
    public User getUser(@PathVariable("uuid") String uuid) {
        return userService.readUser(uuid);
    }

    @PostMapping("/register")
    public Auth registerUser(@RequestBody User user, @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        User userPersistent = userService.createUser(user);
        return authService.generateUserToken(userAgent, userPersistent);
    }

    // @RequestHeader(value = "token", required = false) String token <-- this goes everywhere auth is required

    @PostMapping("/login")
    public Auth loginUser(@RequestHeader("email") String email, @RequestHeader("pin") String pin, @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        User user = userService.getUserByPinAndEmail(pin, email);
        Auth auth = authService.lookupUserToken(user, userAgent);
        if (auth == null) {
            return authService.generateUserToken(userAgent, user);
        }
        return auth;
    }

}
