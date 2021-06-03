package com.agh.EventarzDataService.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NodeEntity("Group")
public class Group {
    @Id
    @GeneratedValue
    @Getter
    private Long id;
    @Getter
    private String uuid;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String createdDate;

    @Getter
    @Setter
    @Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
    private List<User> members;
    @Getter
    @Setter
    @Relationship(type = "PUBLISHED_IN", direction = Relationship.INCOMING)
    private List<Event> events;
    @Getter
    @Setter
    @Relationship(type = "FOUNDED", direction = Relationship.INCOMING)
    private User founder;

    @PersistenceConstructor
    public Group(Long id, String uuid, String name, String description, String createdDate, List<User> members, List<Event> events, User founder) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;

        if (members == null) {
            this.members = new ArrayList<>();
        } else {
            this.members = members;
        }
        if (events == null) {
            this.events = new ArrayList<>();
        } else {
            this.events = events;
        }
        this.founder = founder;
    }

    public Group (GroupForm groupForm, User founder) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String createdDate = LocalDateTime.now().format(dtf);
        this.uuid = UUID.randomUUID().toString();
        this.name = groupForm.getName();
        this.description = groupForm.getDescription();
        this.createdDate = createdDate;
        this.members = new ArrayList<>();
        this.events = new ArrayList<>();
        this.founder = founder;
    }

    public static Group of(String name, String description, List<User> members, List<Event> events, User founder) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String createdDate = LocalDateTime.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new Group(null, uuid, name, description, createdDate, members, events, founder);
    }

    public void joinedBy(User user) {
        members.add(user);
    }

    public void leftBy(String username) {
        members.removeIf(member -> member.getUsername().compareTo(username) == 0);
    }

    public void receiveEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event deletedEvent) {
        events.removeIf(event -> event.getUuid().compareTo(deletedEvent.getUuid()) == 0);
    }

    public boolean containsMember(String username) {
        if (members != null) {
            for (User member : members) {
                if (member.getUsername().compareTo(username) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public GroupDTO createDTO() {
        ArrayList<UserDTO> members = new ArrayList<>();
        for (User member : this.members) {
            members.add(member.createStrippedDTO());
        }

        ArrayList<EventDTO> events = new ArrayList<>();
        for (Event event : this.events) {
            events.add(event.createStrippedDTO());
        }

        return new GroupDTO(
                this.uuid,
                this.name,
                this.description,
                this.createdDate,
                this.members.size(),
                this.events.size(),
                false,
                members,
                events,
                this.founder.createStrippedDTO()
        );
    }

    public GroupDTO createStrippedDTO() {
        return new GroupDTO(
                this.uuid,
                this.name,
                this.description,
                this.createdDate,
                this.members.size(),
                this.events.size(),
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                null
        );
    }

    public String toString() {
        return "Group " + name;
    }
}
