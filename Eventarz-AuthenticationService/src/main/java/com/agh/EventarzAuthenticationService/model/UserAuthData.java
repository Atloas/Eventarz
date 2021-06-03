package com.agh.EventarzAuthenticationService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthData {
    private String uuid;
    private String username;
    private String passwordHash;
    private List<String> roles;
}
