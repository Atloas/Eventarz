package com.agh.EventarzAuthenticationService.controllers;

import com.agh.EventarzAuthenticationService.model.LoginForm;
import com.agh.EventarzAuthenticationService.model.UserForm;
import com.agh.EventarzAuthenticationService.services.UserAuthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// TODO: Put token generation here?

@RestController
public class AuthenticationController {

    @Autowired
    private UserAuthDataService userAuthDataService;

    @GetMapping("/gettest")
    public String test() {
        return "TEST";
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginForm loginForm) {
        if (userAuthDataService.login(loginForm)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserForm userForm) {
        if (userAuthDataService.register(userForm)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
}
