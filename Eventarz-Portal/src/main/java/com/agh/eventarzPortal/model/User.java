package com.agh.eventarzPortal.model;

import com.agh.eventarzPortal.model.serializers.UserSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NodeEntity("User")
@JsonSerialize(using = UserSerializer.class)
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
    private String registerDate;
    @Getter
    @Setter
    @Transient
    private boolean stripped;


    @Getter
    @Setter
    @Relationship(type = "DETAILS_OF", direction = Relationship.INCOMING)
    public SecurityDetails securityDetails;
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

    @JsonCreator
    public User(Long id, String uuid, String username, String registerDate, SecurityDetails securityDetails, boolean stripped, List<Event> events, List<Event> organizedEvents, List<Group> groups, List<Group> foundedGroups) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.registerDate = registerDate;
        this.stripped = stripped;
        if (stripped) {
            this.securityDetails = null;
            this.events = null;
            this.organizedEvents = null;
            this.groups = null;
            this.foundedGroups = null;
        } else {
            this.securityDetails = securityDetails;
            if (events == null) {
                this.events = new ArrayList<>();
            } else {
                this.events = events;
            }
            if (organizedEvents == null) {
                this.organizedEvents = new ArrayList<>();
            } else {
                this.organizedEvents = organizedEvents;
            }
            if (groups == null) {
                this.groups = new ArrayList<>();
            } else {
                this.groups = groups;
            }
            if (foundedGroups == null) {
                this.foundedGroups = new ArrayList<>();
            } else {
                this.foundedGroups = foundedGroups;
            }
        }
    }

    @PersistenceConstructor
    public User(Long id, String uuid, String username, String registerDate, SecurityDetails securityDetails, List<Event> events, List<Event> organizedEvents, List<Group> groups, List<Group> foundedGroups) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.registerDate = registerDate;
        this.securityDetails = securityDetails;
        if (events == null) {
            this.events = new ArrayList<>();
        } else {
            this.events = events;
        }
        if (organizedEvents == null) {
            this.organizedEvents = new ArrayList<>();
        } else {
            this.organizedEvents = organizedEvents;
        }
        if (groups == null) {
            this.groups = new ArrayList<>();
        } else {
            this.groups = groups;
        }
        if (foundedGroups == null) {
            this.foundedGroups = new ArrayList<>();
        } else {
            this.foundedGroups = foundedGroups;
        }
    }

    public User(User that) {
        this(that.id, that.uuid, that.username, that.registerDate, that.securityDetails, that.stripped, that.events, that.organizedEvents,
                that.groups, that.foundedGroups);
    }

    public static User of(String username, SecurityDetails securityDetails, boolean stripped, List<Event> events, List<Event> organizedEvents, List<Group> groups, List<Group> foundedGroups) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String registerDate = LocalDate.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new User(null, uuid, username, registerDate, securityDetails, stripped, events, organizedEvents, groups, foundedGroups);
    }

    public void participatesIn(Event event) {
        events.add(event);
    }

    //TODO: Functions like participatesIn but for removing?

    public void organized(Event event) {
        organizedEvents.add(event);
    }

    public void belongsTo(Group group) {
        groups.add(group);
    }

    public void founded(Group group) {
        foundedGroups.add(group);
    }

    public User withId(Long id) {
        return new User(id, this.uuid, this.username, this.registerDate, this.securityDetails, this.stripped, this.events, this.organizedEvents, this.groups, this.foundedGroups);
    }

    public User createStrippedCopy() {
        return new User(this.id, this.uuid, this.username, this.registerDate, null, true, null, null, null, null);
    }

    public String toString() {
        return "User " + username;
    }
}