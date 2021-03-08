package com.agh.EventarzDataService;

import com.agh.EventarzDataService.model.SecurityDetails;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.model.UserForm;
import com.agh.EventarzDataService.repositories.UserRepository;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    @Transactional
    @Retry(name = "getUserByUsernameRetry")
    User getUserByUsername(@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }

    @GetMapping("/users/uuid")
    @Transactional
    @Retry(name = "getUserUuidByUsernameRetry")
    String getUserUuidByUsername(@RequestParam String username) {
        String uuid = userRepository.findUuidByUsername(username);
        return uuid;
    }

    @GetMapping("/users/security")
    @Transactional
    @Retry(name = "getSecurityDetailsRetry")
    SecurityDetails getSecurityDetails(@RequestParam String username) {
        SecurityDetails securityDetails = userRepository.findDetailsFor(username);
        return securityDetails;
    }

    @GetMapping("/users/regex")
    @Transactional
    @Retry(name = "getUsersByRegexRetry")
    List<User> getUsersByRegex(@RequestParam String regex) {
        List<User> users = userRepository.findByUsernameRegex(regex);
        return users;
    }

    @PostMapping("/users")
    @Transactional
    @Retry(name = "createUserRetry")
    User createUser(@RequestBody UserForm userForm) {
        SecurityDetails securityDetails = SecurityDetails.of(userForm.getPasswordHash(), userForm.getRoles());
        User user = User.of(userForm.getUsername(), securityDetails, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        user = userRepository.save(user);
        return user;
    }

    @DeleteMapping("/users")
    @Transactional
    @Retry(name = "deleteUserRetry")
    Long deleteUser(@RequestParam String username) {
        Long id = userRepository.deleteByUsername(username);
        return id;
    }
}
