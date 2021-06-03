package com.agh.EventarzAuthenticationService.services;

import com.agh.EventarzAuthenticationService.model.LoginForm;
import com.agh.EventarzAuthenticationService.model.SecurityDetails;
import com.agh.EventarzAuthenticationService.model.User;
import com.agh.EventarzAuthenticationService.model.UserForm;
import com.agh.EventarzAuthenticationService.repositories.UserAuthDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserAuthDataService {

    @Autowired
    private UserAuthDataRepository userAuthDataRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean login(LoginForm loginForm) {
        SecurityDetails securityDetails = userAuthDataRepository.findDetailsForUsername(loginForm.getUsername());
        if (securityDetails == null) {
            return false;
        }

        if (securityDetails.getPasswordHash().equals(passwordEncoder.encode(loginForm.getPassword()))) {
            return true;
        }

        return false;
    }

    public boolean register(UserForm userForm) {
        SecurityDetails securityDetails = SecurityDetails.of(userForm.getPasswordHash(), userForm.getRoles());
        User user = User.of(userForm.getUsername(), securityDetails, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        user = userAuthDataRepository.save(user);
        // TODO: This will be simplified when databases split
        return user != null;
    }

    @Bean
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
