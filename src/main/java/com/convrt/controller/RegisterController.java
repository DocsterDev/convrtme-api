package com.convrt.controller;

import com.convrt.entity.User;
import com.convrt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserService userService;


    @PostMapping("")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody User user) {
        log.info("Registering new user");

        User addedUser = userService.addNewUser(user);

        if (null != addedUser) {
            return ResponseEntity.ok().body(addedUser);
        }

        return ResponseEntity
                .badRequest()
                .body("User already exists");
    }
}
