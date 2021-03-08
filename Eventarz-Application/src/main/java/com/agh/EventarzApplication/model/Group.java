package com.agh.EventarzApplication.model;

import com.agh.EventarzApplication.model.serialization.GroupSerializer;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NodeEntity("Group")
@JsonSerialize(using = GroupSerializer.class)
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
    @Transient
    private boolean stripped;

    @Getter
    @Setter
    @Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
    public List<User> members;
    @Getter
    @Setter
    @Relationship(type = "PUBLISHED_IN", direction = Relationship.INCOMING)
    public List<Event> events;
    @Getter
    @Setter
    @Relationship(type = "FOUNDED", direction = Relationship.INCOMING)
    public User founder;

    @JsonCreator
    public Group(Long id, String uuid, String name, String description, String createdDate, boolean stripped, List<User> members, List<Event> events, User founder) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.stripped = stripped;
        if (stripped) {
            this.members = null;
            this.events = null;
            this.founder = null;
        } else {
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
    }

    //All non-transient arguments constructor used by Neo4j
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

    public Group(Group that) {
        this(that.id, that.uuid, that.name, that.description, that.createdDate, that.stripped, that.members, that.events, that.founder);
    }

    public static Group of(String name, String description, boolean stripped, List<User> members, List<Event> events, User founder) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String createdDate = LocalDateTime.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new Group(null, uuid, name, description, createdDate, stripped, members, events, founder);
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

    public Group withId(Long id) {
        return new Group(id, this.uuid, this.name, this.description, this.createdDate, this.stripped, this.members, this.events, this.founder);
    }

    public Group createStrippedCopy() {
        return new Group(this.id, this.uuid, this.name, this.description, this.createdDate, true, null, null, null);
    }

    public String toString() {
        return "Group " + name;
    }
}
