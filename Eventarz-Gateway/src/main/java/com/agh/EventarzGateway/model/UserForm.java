package com.agh.EventarzGateway.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserForm {
    private String username;
    private String password;
    private String passwordHash;
    private List<String> roles;
}
