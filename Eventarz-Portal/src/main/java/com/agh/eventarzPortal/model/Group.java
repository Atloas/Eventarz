package com.agh.eventarzPortal.model;

import com.agh.eventarzPortal.model.serializers.GroupSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
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

    public Group(Group that) {
        this(that.id, that.uuid, that.name, that.description, that.createdDate, that.members, that.events, that.founder);
    }

    public static Group of(String name, String description, List<User> members, List<Event> events, User founder) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String createdDate = LocalDateTime.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new Group(null, uuid, name, description, createdDate, members, events, founder);
    }

    public void joinedBy(User user) {
        if (members == null) {
            members = new ArrayList<>();
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

    public Group createStrippedCopy() {
        return new Group(this.id, this.uuid, this.name, this.description, this.createdDate, null, null, null);
    }

    public String toString() {
        return "Group " + name;
    }
}
