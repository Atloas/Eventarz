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
    private boolean banned;
    @Getter
    @Setter
    private List<String> roles;

    public SecurityDetailsDTO(Long id, String passwordHash, boolean banned, List<String> roles) {
        this.id = id;
        this.passwordHash = passwordHash;
        this.banned = banned;
        this.roles = new ArrayList<String>(roles);
    }

    public static SecurityDetailsDTO of(String passwordHash, boolean banned, List<String> roles) {
        return new SecurityDetailsDTO(null, passwordHash, banned, roles);
    }

    public SecurityDetailsDTO(SecurityDetailsDTO that) {
        this.id = that.id;
        this.passwordHash = that.passwordHash;
        this.banned = that.banned;
        this.roles = new ArrayList<>(that.roles);
    }
}
