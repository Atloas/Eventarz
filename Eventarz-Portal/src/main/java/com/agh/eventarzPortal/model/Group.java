package com.agh.eventarzPortal.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Objects of this class represent Group nodes in the Neo4j database.
 */
@NodeEntity
public class Group {
    /**
     * Neo4j's internal object id, not to be used for actual object identification.
     */
    @Id
    @GeneratedValue
    @Getter
    private Long id;
    /**
     * Uuid serves as a primary key for database objects, since id can't be used for this purpose.
     * Generated on object creation.
     */
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
    public Set<User> members;
    @Getter
    @Setter
    @Relationship(type = "PUBLISHED_IN", direction = Relationship.INCOMING)
    public Set<Event> events;
    @Getter
    @Setter
    @Relationship(type = "FOUNDED", direction = Relationship.INCOMING)
    public User founder;

    /**
     * Parameterless constructor required by Spring Data Neo4j.
     */
    private Group() {
    }

    /**
     * This is the constructor to be used when creating a brand new Event.
     * Apart from setting field values to given parameters, also generates the uuid and founding date.
     *
     * @param name        Name of the group.
     * @param description Description of the group.
     * @param founder     User founding the group.
     */
    public Group(String name, String description, User founder) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.founder = founder;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now.format(dtf);
    }

    /**
     * Adds the specified user to the Group's members list. Creates the list if it's not present.
     *
     * @param user User to be added.
     */
    public void joinedBy(User user) {
        if (members == null) {
            members = new HashSet<>();
        }
        members.add(user);
    }

    /**
     * Attempts to remove the specified user from the members list.
     *
     * @param username User to be removed.
     * @return Whether the user was removed.
     */
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

    /**
     * Checks if the members list contains the specified user.
     *
     * @param username User to check for.
     * @return Whether the user is contained within members.
     */
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

    public String toString() {
        return "Group " + name + "\nMembers: " + members.stream().map(User::getUsername).collect(Collectors.toList());
    }
}
