package com.agh.EventarzApplication.services;

import com.agh.EventarzApplication.exceptions.UserAlreadyExistsException;
import com.agh.EventarzApplication.feignClients.DataClient;
import com.agh.EventarzApplication.model.UserDTO;
import com.agh.EventarzApplication.model.UserForm;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserService {
    @Autowired
    private DataClient dataClient;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Retry(name = "registerNewUserAccount")
    public UserDTO registerNewUserAccount(UserForm userForm)
            throws UserAlreadyExistsException {

        if (checkIfUsernameExists(userForm.getUsername())) {
            throw new UserAlreadyExistsException("There is an account with that username:" + userForm.getUsername());
        }
        userForm.setPasswordHash(passwordEncoder.encode(userForm.getPassword()));
        userForm.setRoles(Arrays.asList("USER"));
        return dataClient.createUser(userForm);
    }

    @Retry(name = "checkIfUsernameExists")
    private boolean checkIfUsernameExists(String username) {
        return dataClient.getUuidByUsername(username) != null;
    }
}