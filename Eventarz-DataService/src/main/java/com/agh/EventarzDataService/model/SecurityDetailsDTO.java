package com.agh.EventarzDataService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class SecurityDetailsDTO {
    @Getter
    private Long id;
    @Getter
    @Setter
    private String passwordHash;
    @Getter
    @Setter
    private List<String> roles;

    public SecurityDetailsDTO(Long id, String passwordHash, List<String> roles) {
        this.id = id;
        this.passwordHash = passwordHash;
        this.roles = new ArrayList<String>(roles);
    }

    public static SecurityDetailsDTO of(String passwordHash, List<String> roles) {
        return new SecurityDetailsDTO(null, passwordHash, roles);
    }

    public SecurityDetailsDTO(SecurityDetailsDTO that) {
        this.id = that.id;
        this.passwordHash = that.passwordHash;
        this.roles = new ArrayList<>(that.roles);
    }
}
