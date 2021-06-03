package com.agh.EventarzGateway.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SecurityDetails {
    private String passwordHash;
    private boolean banned;
    private List<String> roles;
}
