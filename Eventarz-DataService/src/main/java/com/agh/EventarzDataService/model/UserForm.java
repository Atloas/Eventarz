package com.agh.EventarzDataService.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {
    private String username;
    private String passwordHash;
    private List<String> roles;
}
