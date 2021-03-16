package com.agh.EventarzDataService.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NodeEntity("User")
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
    private SecurityDetails securityDetails;
    @Getter
    @Setter
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.OUTGOING)
    private List<Event> events;
    @Getter
    @Setter
    @Relationship(type = "ORGANIZED", direction = Relationship.OUTGOING)
    private List<Event> organizedEvents;
    @Getter
    @Setter
    @Relationship(type = "BELONGS_TO", direction = Relationship.OUTGOING)
    private List<Group> groups;
    @Getter
    @Setter
    @Relationship(type = "FOUNDED", direction = Relationship.OUTGOING)
    private List<Group> foundedGroups;

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

    public static User of(String username, SecurityDetails securityDetails, List<Event> events, List<Event> organizedEvents, List<Group> groups, List<Group> foundedGroups) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String registerDate = LocalDate.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new User(null, uuid, username, registerDate, securityDetails, events, organizedEvents, groups, foundedGroups);
    }

    public void participatesIn(Event event) {
        events.add(event);
    }

    //TODO: Create functions like participatesIn but for removing?
    public void organized(Event event) {
        organizedEvents.add(event);
    }

    public void belongsTo(Group group) {
        groups.add(group);
    }

    public void founded(Group group) {
        foundedGroups.add(group);
    }

    public UserDTO createDTO() {
        ArrayList<EventDTO> events = new ArrayList<>();
        for (Event event : this.events) {
            events.add(event.createStrippedDTO());
        }

        ArrayList<EventDTO> organizedEvents = new ArrayList<>();
        for (Event organizedEvent : this.organizedEvents) {
            events.add(organizedEvent.createStrippedDTO());
        }

        ArrayList<GroupDTO> groups = new ArrayList<>();
        for (Group group : this.groups) {
            groups.add(group.createStrippedDTO());
        }

        ArrayList<GroupDTO> foundedGroups = new ArrayList<>();
        for (Group foundedGroup : this.foundedGroups) {
            foundedGroups.add(foundedGroup.createStrippedDTO());
        }

        return new UserDTO(
                this.id,
                this.uuid,
                this.username,
                this.registerDate,
                false,
                this.securityDetails.createDTO(),
                events,
                organizedEvents,
                groups,
                foundedGroups
        );
    }

    public UserDTO createStrippedDTO() {
        return new UserDTO(
                this.id,
                this.uuid,
                this.username,
                this.registerDate,
                true,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public String toString() {
        return "User " + username;
    }
}