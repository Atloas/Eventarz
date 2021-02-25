package com.agh.EventarzEventService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Node("Group")
public class Group {
    @Id
    @GeneratedValue
    @Getter
    private final Long id;
    @GeneratedValue(UUIDStringGenerator.class)
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
    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    public Set<User> members;
    @Getter
    @Setter
    @Relationship(type = "PUBLISHED_IN", direction = Relationship.Direction.INCOMING)
    public Set<Event> events;
    @Getter
    @Setter
    @Relationship(type = "FOUNDED", direction = Relationship.Direction.INCOMING)
    public User founder;

    public static Group of(String name, String description, Set<User> members, Set<Event> events, User founder) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String createdDate = LocalDateTime.now().format(dtf);
        return new Group(null, null, name, description, createdDate, members, events, founder);
    }

    public void joinedBy(User user) {
        if (members == null) {
            members = new HashSet<>();
        }
        members.add(user);
    }

    public boolean leftBy(String username) {
        if (members != null) {
            Iterator<User> iterator = members.iterator();
            while (iterator.hasNext()) {
                User member = iterator.next();
                if (member.getUsername().compareTo(username) == 0) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
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
        return new Group(id, this.uuid, this.name, this.description, this.createdDate, this.members, this.events, this.founder);
    }

    public String toString() {
        return "Group " + name + "\nMembers: " + members.stream().map(User::getUsername).collect(Collectors.toList());
    }
}
