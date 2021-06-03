package com.agh.EventarzDataService.controllers;

import com.agh.EventarzDataService.exceptions.SecurityDetailsNotFoundException;
import com.agh.EventarzDataService.exceptions.UserNotFoundException;
import com.agh.EventarzDataService.model.BanForm;
import com.agh.EventarzDataService.model.SecurityDetailsDTO;
import com.agh.EventarzDataService.model.UserDTO;
import com.agh.EventarzDataService.model.UserForm;
import com.agh.EventarzDataService.services.UserService;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
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

    @GetMapping("/users/{username}")
    @Transactional
    @Retry(name = "getUserByUsernameRetry")
    public UserDTO getUserByUsername(@PathVariable String username) {
        try {
            UserDTO userDTO = userService.getUserByUsername(username);
            return userDTO;
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!", e);
        }
    }

    @DeleteMapping("/users/{username}")
    @Transactional
    @Retry(name = "deleteUserRetry")
    public String deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return username;
    }

    // TODO: Remove Transactional from simple queries?
    @RequestMapping(method = RequestMethod.HEAD, value = "/users/{username}")
    @Transactional
    @Retry(name = "getUserUuidByUsernameRetry")
    public void checkIfUserExists(@PathVariable String username) {
        try {
            userService.checkIfUserExists(username);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!", e);
        }
    }

    @GetMapping("/users/{username}/securityDetails")
    @Transactional
    @Retry(name = "getSecurityDetailsRetry")
    public SecurityDetailsDTO getSecurityDetails(@PathVariable String username) {
        try {
            SecurityDetailsDTO securityDetails = userService.getSecurityDetails(username);
            return securityDetails;
        } catch (SecurityDetailsNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SecurityDetails not found!", e);
        }
    }

    @PutMapping("users/{username}/securityDetails/banned")
    public UserDTO changeBanStatus(@PathVariable String username, @RequestBody BanForm banForm) {
        try {
            UserDTO userDTO = userService.changeBanStatus(username, banForm);
            return userDTO;
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!", e);
        }
    }
}
