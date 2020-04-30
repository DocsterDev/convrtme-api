package com.convrt.api.controller;

import com.convrt.api.entity.Context;
import com.convrt.api.service.ContextService;
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
    private ContextService contextService;

    // @RequestHeader(value = "token", required = false) String token <-- this goes everywhere auth is required

    @PostMapping("/authenticate")
    public Context authenticate(@RequestHeader("User-Agent") String userAgent, @RequestHeader("token") String token){
        return contextService.validateContext(token, userAgent);
    }

    @PostMapping("/logout")
    public Context logoutUser(@RequestBody Context context){
        return contextService.userLogout(context);
    }

}
