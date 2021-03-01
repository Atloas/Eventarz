package com.agh.EventarzEventService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@NodeEntity
public class User {
    @Id
    @GeneratedValue
    @Getter
    private Long id;
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
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.OUTGOING)
    public List<Event> events;
    @Getter
    @Setter
    @Relationship(type = "ORGANIZED", direction = Relationship.OUTGOING)
    public List<Event> organizedEvents;
    @Getter
    @Setter
    @Relationship(type = "BELONGS_TO", direction = Relationship.OUTGOING)
    public List<Group> groups;
    @Getter
    @Setter
    @Relationship(type = "FOUNDED", direction = Relationship.OUTGOING)
    public List<Group> foundedGroups;

    public User(User that) {
        this(that.id, that.uuid, that.username, that.passwordHash, that.registerDate, that.roles, that.events, that.organizedEvents,
                that.groups, that.foundedGroups);
    }

    public static User of(String username, String passwordHash, List<String> roles, List<Event> events, List<Event> organizedEvents, List<Group> groups, List<Group> foundedGroups) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String registerDate = LocalDate.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new User(null, uuid, username, passwordHash, registerDate, roles, events, organizedEvents, groups, foundedGroups);
    }

    public void participatesIn(Event event) {
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(event);
    }

    public void organized(Event event) {
        if (organizedEvents == null) {
            organizedEvents = new ArrayList<>();
        }
        organizedEvents.add(event);
    }

    public void belongsTo(Group group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(group);
    }

    public void founded(Group group) {
        if (foundedGroups == null) {
            foundedGroups = new ArrayList<>();
        }
        foundedGroups.add(group);
    }

    public User withId(Long id) {
        return new User(id, this.uuid, this.username, this.passwordHash, this.registerDate, this.roles, this.events, this.organizedEvents, this.groups, this.foundedGroups);
    }

    public User createSerializableCopy() {
        List<Event> events = new ArrayList<>();
        if (this.events != null) {
            for (Event event : this.getEvents()) {
                events.add(event.createStrippedCopy());
            }
        }
        List<Event> organizedEvents = new ArrayList<>();
        if (this.organizedEvents != null) {
            for (Event event : this.getOrganizedEvents()) {
                organizedEvents.add(event.createStrippedCopy());
            }
        }
        List<Group> groups = new ArrayList<>();
        if (this.groups != null) {
            for (Group group : this.getGroups()) {
                groups.add(group.createStrippedCopy());
            }
        }
        List<Group> foundedGroups = new ArrayList<>();
        if (this.foundedGroups != null) {
            for (Group group : this.getGroups()) {
                foundedGroups.add(group.createStrippedCopy());
            }
        }
        return new User(this.id, this.uuid, this.username, this.passwordHash, this.registerDate, this.roles, events, organizedEvents, groups, foundedGroups);
    }

    //Strips data references at depth 1 to avoid circular references
    public void prepareForSerialization() {
        if (this.events != null) {
            for (Event event : this.events) {
                event.stripReferences();
            }
        }
        if (this.organizedEvents != null) {
            for (Event organizedEvent : this.organizedEvents) {
                organizedEvent.stripReferences();
            }
        }
        if (this.groups != null) {
            for (Group group : this.groups) {
                group.stripReferences();
            }
        }
        if (this.foundedGroups != null) {
            for (Group foundedGroup : this.foundedGroups) {
                foundedGroup.stripReferences();
            }
        }
    }

    public User createStrippedCopy() {
        return new User(this.id, this.uuid, this.username, this.passwordHash, this.registerDate, this.roles, null, null, null, null);
    }

    //Strips database object references stemming from this object
    void stripReferences() {
        this.groups = null;
        this.foundedGroups = null;
        this.events = null;
        this.organizedEvents = null;
    }

    public String toString() {
        return "User " + username;
    }
}