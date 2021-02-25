package com.agh.EventarzEventService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Node("User")
public class User {
    @Id
    @GeneratedValue
    @Getter
    private final Long id;
    @GeneratedValue(UUIDStringGenerator.class)
    @Getter
    private String uuid;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String passwordHash;
    @Getter
    @Setter
    private String registerDate;
    @Getter
    @Setter
    private List<String> roles;


    @Getter
    @Setter
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.Direction.OUTGOING)
    public Set<Event> events;
    @Getter
    @Setter
    @Relationship(type = "ORGANIZED", direction = Relationship.Direction.OUTGOING)
    public Set<Event> organizedEvents;
    @Getter
    @Setter
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    public Set<Group> groups;
    @Getter
    @Setter
    @Relationship(type = "FOUNDED", direction = Relationship.Direction.OUTGOING)
    public Set<Group> foundedGroups;

    public static User of(String username, String passwordHash, List<String> roles, Set<Event> events, Set<Event> organizedEvents, Set<Group> groups, Set<Group> foundedGroups) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String registerDate = LocalDate.now().format(dtf);
        return new User(null, null, username, passwordHash, registerDate, roles, events, organizedEvents, groups, foundedGroups);
    }

    public void participatesIn(Event event) {
        if (events == null) {
            events = new HashSet<>();
        }
        events.add(event);
    }

    public void organized(Event event) {
        if (organizedEvents == null) {
            organizedEvents = new HashSet<>();
        }
        organizedEvents.add(event);
    }

    public void belongsTo(Group group) {
        if (groups == null) {
            groups = new HashSet<>();
        }
        groups.add(group);
    }

    public void founded(Group group) {
        if (foundedGroups == null) {
            foundedGroups = new HashSet<>();
        }
        foundedGroups.add(group);
    }

    public User withId(Long id) {
        return new User(id, this.uuid, this.username, this.passwordHash, this.registerDate, this.roles, this.events, this.organizedEvents, this.groups, this.foundedGroups);
    }

    public String toString() {
        return "User " + username;
    }
}