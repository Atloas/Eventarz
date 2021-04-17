package com.agh.EventarzDataService.controllers;

import com.agh.EventarzDataService.model.SecurityDetailsDTO;
import com.agh.EventarzDataService.model.UserDTO;
import com.agh.EventarzDataService.model.UserForm;
import com.agh.EventarzDataService.services.UserService;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @Transactional
    @Retry(name = "getUserByUsernameRetry")
    public UserDTO getUserByUsername(@RequestParam String username) {
        UserDTO userDTO = userService.getUserByUsername(username);
        return userDTO;
    }

    @GetMapping("/users/uuid")
    @Transactional
    @Retry(name = "getUserUuidByUsernameRetry")
    public String getUserUuidByUsername(@RequestParam String username) {
        String uuid = userService.getUserUuidByUsername(username);
        return uuid;
    }

    @GetMapping("/users/security")
    @Transactional
    @Retry(name = "getSecurityDetailsRetry")
    public SecurityDetailsDTO getSecurityDetails(@RequestParam String username) {
        SecurityDetailsDTO securityDetailsDTO = userService.getSecurityDetails(username);
        return securityDetailsDTO;
    }

    @GetMapping("/users/regex")
    @Transactional
    @Retry(name = "getUsersByRegexRetry")
    public List<UserDTO> getUsersByRegex(@RequestParam String regex) {
        List<UserDTO> userDTOs = userService.getUsersByRegex(regex);
        return userDTOs;
    }

    @PostMapping("/users")
    @Transactional
    @Retry(name = "createUserRetry")
    public UserDTO createUser(@RequestBody UserForm userForm) {
        UserDTO userDTO = userService.createUser(userForm);
        return userDTO;
    }

    @DeleteMapping("/users")
    @Transactional
    @Retry(name = "deleteUserRetry")
    public Long deleteUser(@RequestParam String username) {
        Long id = userService.deleteUser(username);
        return id;
    }
}
