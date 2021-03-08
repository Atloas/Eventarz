package com.agh.EventarzApplication;

import com.agh.EventarzApplication.feignClients.DataClient;
import com.agh.EventarzApplication.model.User;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles User data retrieval from the database for the purpose of logging in.
 */
@Service
public class EventarzUserDetailsService implements UserDetailsService {

    @Autowired
    private DataClient dataClient;

    @Override
    @Retry(name = "loadUserByUsername")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = dataClient.getUser(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user found for username " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getSecurityDetails().getPasswordHash(), true, true,
                true, true,
                getAuthorities(user.getSecurityDetails().getRoles())
        );
    }

    /**
     * Transforms the roles list from the User object into a list of Spring GrantedAuthority objects.
     *
     * @param roles A list of roles.
     * @return A list of GrantedAuthority objects.
     */
    private static List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
