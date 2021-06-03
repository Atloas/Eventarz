package com.agh.EventarzDataService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@NodeEntity("SecurityDetails")
public class SecurityDetails {
    @Id
    @GeneratedValue
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

    public static SecurityDetails of(String passwordHash, boolean banned, List<String> roles) {
        return new SecurityDetails(null, passwordHash, banned, roles);
    }

    public SecurityDetails(SecurityDetails that) {
        this.id = that.id;
        this.passwordHash = that.passwordHash;
        this.roles = new ArrayList<>(that.roles);
    }

    public SecurityDetailsDTO createDTO() {
        return new SecurityDetailsDTO(this.id, this.passwordHash, this.banned, this.roles);
    }
}
