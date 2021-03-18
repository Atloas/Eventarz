package com.agh.EventarzDataService;

import com.agh.EventarzDataService.model.SecurityDetails;
import com.agh.EventarzDataService.model.SecurityDetailsDTO;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.model.UserDTO;
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
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    @Transactional
    @Retry(name = "getUserByUsernameRetry")
    UserDTO getUserByUsername(@RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        UserDTO userDTO = null;
        if (user.isPresent()) {
            userDTO = user.get().createDTO();
        }
        return userDTO;
    }

    @GetMapping("/users/uuid")
    @Transactional
    @Retry(name = "getUserUuidByUsernameRetry")
    String getUserUuidByUsername(@RequestParam String username) {
        return userRepository.findUuidByUsername(username).get();
    }

    @GetMapping("/users/security")
    @Transactional
    @Retry(name = "getSecurityDetailsRetry")
    SecurityDetailsDTO getSecurityDetails(@RequestParam String username) {
        Optional<SecurityDetails> securityDetails = userRepository.findDetailsFor(username);
        SecurityDetailsDTO securityDetailsDTO = null;
        if (securityDetails.isPresent()) {
            securityDetailsDTO = securityDetails.get().createDTO();
        }
        return securityDetailsDTO;
    }

    @GetMapping("/users/regex")
    @Transactional
    @Retry(name = "getUsersByRegexRetry")
    List<UserDTO> getUsersByRegex(@RequestParam String regex) {
        List<User> users = userRepository.findByUsernameRegex(regex);
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(user.createDTO());
        }
        return userDTOs;
    }

    @PostMapping("/users")
    @Transactional
    @Retry(name = "createUserRetry")
    UserDTO createUser(@RequestBody UserForm userForm) {
        SecurityDetails securityDetails = SecurityDetails.of(userForm.getPasswordHash(), userForm.getRoles());
        User user = User.of(userForm.getUsername(), securityDetails, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        user = userRepository.save(user);
        UserDTO userDTO = user.createDTO();
        return userDTO;
    }

    @DeleteMapping("/users")
    @Transactional
    @Retry(name = "deleteUserRetry")
    Long deleteUser(@RequestParam String username) {
        return userRepository.deleteByUsername(username).get();
    }
}
