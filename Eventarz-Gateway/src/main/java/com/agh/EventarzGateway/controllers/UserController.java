package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.exceptions.UserAlreadyExistsException;
import com.agh.EventarzGateway.model.RegisterForm;
import com.agh.EventarzGateway.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public void register(@Valid @RequestBody RegisterForm registerForm) {
        try {
            userService.register(registerForm);
        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username taken!", e);
        }
    }
}
