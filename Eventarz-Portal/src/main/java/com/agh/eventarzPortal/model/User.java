package com.agh.eventarzPortal.model;

import com.agh.eventarzPortal.model.serializers.UserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

    public User(User that) {
        this(that.id, that.uuid, that.username, that.registerDate, that.securityDetails, that.events, that.organizedEvents,
                that.groups, that.foundedGroups);
    }

    public static User of(String username, SecurityDetails securityDetails, List<Event> events, List<Event> organizedEvents, List<Group> groups, List<Group> foundedGroups) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String registerDate = LocalDate.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new User(null, uuid, username, registerDate, securityDetails, events, organizedEvents, groups, foundedGroups);
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
        return new User(id, this.uuid, this.username, this.registerDate, this.securityDetails, this.events, this.organizedEvents, this.groups, this.foundedGroups);
    }

    public User createStrippedCopy() {
        return new User(this.id, this.uuid, this.username, this.registerDate, this.securityDetails, null, null, null, null);
    }

    public String toString() {
        return "User " + username;
    }
}