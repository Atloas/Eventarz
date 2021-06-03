package com.agh.EventarzDataService.services;

import com.agh.EventarzDataService.exceptions.SecurityDetailsNotFoundException;
import com.agh.EventarzDataService.exceptions.UserNotFoundException;
import com.agh.EventarzDataService.model.BanForm;
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

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getUsersByRegex(String regex) {
        List<User> users = userRepository.findByUsernameRegex(regex);
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(user.createDTO());
        }
        return userDTOs;
    }

    public UserDTO createUser(UserForm userForm) {
        SecurityDetails securityDetails = SecurityDetails.of(userForm.getPasswordHash(), false, userForm.getRoles());
        User user = User.of(userForm.getUsername(), securityDetails, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        user = userRepository.save(user);
        return user.createDTO();
    }

    public UserDTO getUserByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User " + username + " not found!");
        }
        UserDTO userDTO = user.createDTO();
        return userDTO;
    }

    public void checkIfUserExists(String username) throws UserNotFoundException {
        boolean exists = userRepository.checkIfUserExists(username);
        if (!exists) {
            throw new UserNotFoundException("User " + username + " not found!");
        }
    }

    public SecurityDetailsDTO getSecurityDetails(String username) throws SecurityDetailsNotFoundException {
        // TODO: Switch to findUSer and pull details from there? If User is present but their SecurityDetails aren't that's a way bigger issue
        SecurityDetails securityDetails = userRepository.findDetailsFor(username);
        if (securityDetails == null) {
            throw new SecurityDetailsNotFoundException("Security details for user " + username + " not found!");
        }
        SecurityDetailsDTO securityDetailsDTO = securityDetails.createDTO();
        return securityDetailsDTO;
    }

    public String deleteUser(String username) {
        userRepository.deleteByUsername(username);
        return username;
    }

    public UserDTO changeBanStatus(String username, BanForm banForm) throws UserNotFoundException {
        // TODO: Replace with some simple exists check?
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User " + username + " not found!");
        }
        if (banForm.isBanned()) {
            banUser(username);
        } else {
            unbanUser(username);
        }
        user = userRepository.findByUsername(username);
        UserDTO userDTO = user.createDTO();
        return userDTO;
    }

    private void banUser(String username) {
        userRepository.changeBanStatus(username, true);
        userRepository.removeFromEvents(username);
        userRepository.removeFromGroups(username);
    }

    private void unbanUser(String username) {
        userRepository.changeBanStatus(username, false);
    }
}
