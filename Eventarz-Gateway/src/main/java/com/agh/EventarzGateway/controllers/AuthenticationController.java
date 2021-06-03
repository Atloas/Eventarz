package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.config.JwtUtility;
import com.agh.EventarzGateway.feignClients.DataClient;
import com.agh.EventarzGateway.model.LoginForm;
import com.agh.EventarzGateway.model.SecurityDetails;
import com.agh.EventarzGateway.model.dtos.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private DataClient dataClient;

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginForm loginForm) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));

            //TODO Cleanup! This should be in an AuthenticationService or something.
            User user = (User) authenticate.getPrincipal();

            String token = jwtUtility.generateAccessToken(user);

            List<String> roles = new ArrayList<>();
            for (GrantedAuthority authority : user.getAuthorities()) {
                roles.add(authority.toString());
            }
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(token, user.getUsername(), roles);
            return loginResponseDTO;
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password!", e);
        } catch (LockedException e) {
            // This fires before the password check, so you can check somebody else's ban state knowing their username.
            // It shouldn't work like that, but I'm not going to fix it.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account locked!", e);
        }
    }

    @PostMapping("/refreshLogin")
    public LoginResponseDTO refreshLogin(Principal principal) {
        String username = principal.getName();
        SecurityDetails securityDetails = dataClient.getSecurityDetails(username);
        String token = jwtUtility.generateAccessToken(username);
        return new LoginResponseDTO(token, username, securityDetails.getRoles());
    }
}
