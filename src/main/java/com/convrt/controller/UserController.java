package com.convrt.controller;

import com.convrt.entity.Context;
import com.convrt.entity.Playlist;
import com.convrt.entity.User;
import com.convrt.service.ContextService;
import com.convrt.service.PlaylistService;
import com.convrt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PlaylistService playlistService;
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
