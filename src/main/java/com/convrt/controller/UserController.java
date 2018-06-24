package com.convrt.controller;

import com.convrt.entity.User;
import com.convrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.readUsers();
    }

    @GetMapping("/{uuid}")
    public User getUser(@PathVariable("uuid") String uuid) {
        return userService.readUser(uuid);
    }

    @PostMapping("/register")
    public User createUser(@RequestHeader(value = "User-Agent", required = false) String userAgent, @RequestBody User user) {
        String email = user.getEmail();
        if (userService.existsByEmail(email)) {
            throw new RuntimeException("User already exists for email address " + email);
        }
        user.setUuid(UUID.randomUUID().toString());
        user.setUserAgent(userAgent);
        return userService.createUser(user);
    }

    @PostMapping
    public User createAnonymousUser(@RequestHeader(value = "User-Agent", required = false) String userAgent) {
        String email = user.getEmail();
        if (userService.existsByEmail(email)) {
            throw new RuntimeException("User already exists for email address " + email);
        }
        user.setUuid(UUID.randomUUID().toString());
        user.setUserAgent(userAgent);
        return userService.createUser(user);
    }

}
