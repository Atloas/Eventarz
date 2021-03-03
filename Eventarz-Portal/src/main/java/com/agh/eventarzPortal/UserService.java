package com.agh.eventarzPortal;

import com.agh.eventarzPortal.feignClients.DataClient;
import com.agh.eventarzPortal.model.User;
import com.agh.eventarzPortal.model.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class UserService {
    @Autowired
    private DataClient dataClient;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerNewUserAccount(UserForm userForm)
            throws UserAlreadyExistsException {

        if (usernameExists(userForm.getUsername())) {
            throw new UserAlreadyExistsException("There is an account with that username:" + userForm.getUsername());
        }
        userForm.setPasswordHash(passwordEncoder.encode(userForm.getPassword()));
        userForm.setRoles(Arrays.asList("USER"));
        return dataClient.createUser(userForm);
    }

    private boolean usernameExists(String username) {
        return dataClient.getUuidByUsername(username) != null;
    }
}