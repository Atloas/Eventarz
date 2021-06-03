package com.agh.EventarzAuthenticationService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;

// TODO: Store auth data in a separate, SQL database.
// TODO: Rip out all those Neo4j related objects when ^ is done

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
    private List<String> roles;

    public static SecurityDetails of(String passwordHash, List<String> roles) {
        return new SecurityDetails(null, passwordHash, roles);
    }

    public SecurityDetails(SecurityDetails that) {
        this.id = that.id;
        this.passwordHash = that.passwordHash;
        this.roles = new ArrayList<>(that.roles);
    }

    public SecurityDetailsDTO createDTO() {
        return new SecurityDetailsDTO(this.id, this.passwordHash, this.roles);
    }
}
