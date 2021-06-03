package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.exceptions.UserAlreadyExistsException;
import com.agh.EventarzGateway.feignClients.DataClient;
import com.agh.EventarzGateway.model.BanForm;
import com.agh.EventarzGateway.model.SecurityDetails;
import com.agh.EventarzGateway.model.User;
import com.agh.EventarzGateway.model.RegisterForm;
import com.agh.EventarzGateway.model.UserForm;
import com.agh.EventarzGateway.model.dtos.UserDTO;
import com.agh.EventarzGateway.model.dtos.UserShortDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private DataClient dataClient;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterForm registerForm) throws UserAlreadyExistsException {
        if (checkIfUsernameExists(registerForm.getUsername())) {
            throw new UserAlreadyExistsException("There already is an account with username " + registerForm.getUsername() + "!");
        }
        UserForm userForm = new UserForm();
        userForm.setUsername(registerForm.getUsername());
        userForm.setPasswordHash(passwordEncoder.encode(registerForm.getPassword()));
        userForm.setRoles(Arrays.asList("USER"));
        User user = dataClient.createUser(userForm);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Rethink authentication data flow
        SecurityDetails securityDetails = dataClient.getSecurityDetails(username);
        if (securityDetails == null) {
            throw new UsernameNotFoundException("No user found for username " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                username,
                securityDetails.getPasswordHash(), true, true,
                true, !securityDetails.isBanned(),
                getAuthorities(securityDetails.getRoles())
        );
    }

    public List<UserShortDTO> getUsersByRegex(String username) {
        List<User> users = dataClient.getUsersByRegex("(?i).*" + username + ".*");
        List<UserShortDTO> userShortDTOs = new ArrayList<>();
        for (User user : users) {
            userShortDTOs.add(new UserShortDTO(user));
        }
        return userShortDTOs;
    }

    public UserDTO getUser(String username) {
        User user = dataClient.getUser(username);
        UserDTO userDTO = new UserDTO(user);
        return userDTO;
    }

    public UserDTO changeBannedStatus(String username, BanForm banForm) {
        User user = dataClient.changeBanStatus(username, banForm);
        UserDTO userDTO = new UserDTO(user);
        return userDTO;
    }

    private boolean checkIfUsernameExists(String username) {
        try {
            dataClient.checkIfUserExists(username);
            return true;
        } catch (FeignException e) {
            if (e.status() != HttpStatus.NOT_FOUND.value()) {
                throw e;
            }
            return false;
        }
    }

    private static List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

}