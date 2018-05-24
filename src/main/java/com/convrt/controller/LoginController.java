package com.convrt.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = LogManager.getLogger(LoginController.class);

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String login() {
        return "Login";
    }


}
