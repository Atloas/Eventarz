package com.agh.EventarzApplication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@NodeEntity("SecurityDetails")
public class SecurityDetails {
    @Id
    @GeneratedValue
    @Getter
    Long id;
    @Getter
    @Setter
    String passwordHash;
    @Getter
    @Setter
    List<String> roles;

    public static SecurityDetails of(String passwordHash, List<String> roles) {
        return new SecurityDetails(null, passwordHash, roles);
    }

    public SecurityDetails withId(Long id) {
        return new SecurityDetails(id, this.passwordHash, this.roles);
    }
}
