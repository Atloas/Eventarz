package com.agh.EventarzDataService.services;

import com.agh.EventarzDataService.model.SecurityDetails;
import com.agh.EventarzDataService.model.SecurityDetailsDTO;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.model.UserDTO;
import com.agh.EventarzDataService.model.UserForm;
import com.agh.EventarzDataService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        UserDTO userDTO = null;
        if (user.isPresent()) {
            userDTO = user.get().createDTO();
        }
        return userDTO;
    }

    public String getUserUuidByUsername(String username) {
        return userRepository.findUuidByUsername(username).orElse(null);
    }

    public SecurityDetailsDTO getSecurityDetails(String username) {
        Optional<SecurityDetails> securityDetails = userRepository.findDetailsFor(username);
        SecurityDetailsDTO securityDetailsDTO = null;
        if (securityDetails.isPresent()) {
            securityDetailsDTO = securityDetails.get().createDTO();
        }
        return securityDetailsDTO;
    }

    public List<UserDTO> getUsersByRegex(String regex) {
        List<User> users = userRepository.findByUsernameRegex(regex);
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(user.createDTO());
        }
        return userDTOs;
    }

    public UserDTO createUser(UserForm userForm) {
        SecurityDetails securityDetails = SecurityDetails.of(userForm.getPasswordHash(), userForm.getRoles());
        User user = User.of(userForm.getUsername(), securityDetails, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        user = userRepository.save(user);
        return user.createDTO();
    }

    public Long deleteUser(String username) {
        return userRepository.deleteByUsername(username).orElse(null);
    }
}
