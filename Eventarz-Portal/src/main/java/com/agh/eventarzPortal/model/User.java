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
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Objects of this class represent User nodes in the Neo4j database.
 */
@NodeEntity
public class User {
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
    @Id
    private String username;
    @Getter
    @Setter
    private String passwordHash;
    @Getter
    @Setter
    private String registerDate;
    /**
     * A LocalDateTime object containing the same data as the registerDate field.
     * Created the first time {@link #getRegisterDateObject} is called.
     */
    @Setter
    private LocalDateTime registerDateObject;
    /**
     * Contains the security related roles of the User, either USER or ADMIN.
     */
    @Getter
    @Setter
    private List<String> roles;


    @Getter
    @Setter
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.OUTGOING)
    public Set<Event> events;
    @Getter
    @Setter
    @Relationship(type = "ORGANIZED", direction = Relationship.OUTGOING)
    public Set<Event> organizedEvents;
    @Getter
    @Setter
    @Relationship(type = "BELONGS_TO", direction = Relationship.OUTGOING)
    public Set<Group> groups;
    @Getter
    @Setter
    @Relationship(type = "FOUNDED", direction = Relationship.OUTGOING)
    public Set<Group> foundedGroups;

    /**
     * Parameterless constructor required by Spring Data Neo4j.
     */
    private User() {
    }

    /**
     * This is the constructor to be used when creating a brand new User.
     * Apart from setting field values to given parameters, also generates the uuid and registration date.
     *
     * @param username     Name of the user.
     * @param passwordHash Hashed password.
     * @param roles        Security roles.
     */
    public User(String username, String passwordHash, List<String> roles) {
        this.uuid = UUID.randomUUID().toString();
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        this.registerDate = now.format(dtf);
    }

    /**
     * Returns the User's registerDate in the form of a LocalDateTime object, creates it if not already present.
     *
     * @return LocalDateTime object pointing to registerDate.
     */
    public LocalDateTime getRegisterDateObject() {
        if (registerDateObject == null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            registerDateObject = LocalDateTime.parse(registerDate, dtf);
        }
        return registerDateObject;
    }

    /**
     * Adds the given Event to the User's events list.
     *
     * @param event Event to be added.
     */
    public void participatesIn(Event event) {
        if (events == null) {
            events = new HashSet<>();
        }
        events.add(event);
    }

    /**
     * Adds the given Event to the User's organizedEvents list.
     *
     * @param event Event to be added.
     */
    public void organized(Event event) {
        if (organizedEvents == null) {
            organizedEvents = new HashSet<>();
        }
        organizedEvents.add(event);
    }

    /**
     * Adds the given Group to the User's groups list.
     *
     * @param group Group to be added.
     */
    public void belongsTo(Group group) {
        if (groups == null) {
            groups = new HashSet<>();
        }
        groups.add(group);
    }

    /**
     * Adds the given Group to the User's foundedGroups list.
     *
     * @param group Group to be added.
     */
    public void founded(Group group) {
        if (foundedGroups == null) {
            foundedGroups = new HashSet<>();
        }
        foundedGroups.add(group);
    }

    public String toString() {
        return "User " + username;
    }
}