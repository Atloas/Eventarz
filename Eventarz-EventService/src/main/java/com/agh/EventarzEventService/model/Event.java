package com.agh.EventarzEventService.model;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Objects of this class represent Event nodes in the Neo4j database.
 */
@NodeEntity
public class Event {
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
    private int maxParticipants;
    @Getter
    @Setter
    private String eventDate;
    /**
     * A LocalDateTime object containing the same data as the eventDate field.
     * Created the first time {@link #getEventDateObject} is called.
     */
    @Setter
    @Transient
    private LocalDateTime eventDateObject;
    /**
     * The date this Event was created at.
     * Generated automatically.
     */
    @Getter
    @Setter
    private String publishedDate;
    /**
     * A LocalDateTime object containing the same data as the publishedDate field.
     * Created the first time {@link #getPublishedDateObject} is called.
     */
    @Setter
    @Transient
    private LocalDateTime publishedDateObject;
    /**
     * Whether the event is in the past or not.
     */
    @Getter
    @Setter
    @Transient
    private boolean expired;

    @Getter
    @Setter
    @Relationship(type = "ORGANIZED", direction = Relationship.INCOMING)
    public User organizer;
    @Getter
    @Setter
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.INCOMING)
    public Set<User> participants;
    @Getter
    @Setter
    @Relationship(type = "PUBLISHED_IN", direction = Relationship.OUTGOING)
    public Group group;

    /**
     * Parameterless constructor required by Spring Data Neo4j.
     */
    private Event() {
    }

    /**
     * This is the constructor to be used when creating a brand new Event.
     * Apart from setting field values to given parameters, also generates the uuid and publishing date.
     *
     * @param name            Name of the event.
     * @param organizer       User organizing the event.
     * @param group           Group within which the event is posted.
     * @param description     Description of the event.
     * @param maxParticipants Maximum allowed participants.
     * @param eventDate       When the event is supposed to take place.
     */
    public Event(String name, User organizer, Group group, String description, int maxParticipants, String eventDate) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.organizer = organizer;
        this.group = group;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.eventDate = eventDate;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        this.publishedDate = now.format(dtf);
    }

    /**
     * Attempts to add a user to this Event's participants list. Creates the list if it's not present.
     *
     * @param user User that wishes to participate in this Event.
     * @return Whether adding a participant was successful.
     */
    public boolean participatedBy(User user) {
        if (participants == null) {
            participants = new HashSet<>();
            participants.add(user);
            return true;
        } else if (participants.size() < maxParticipants) {
            participants.add(user);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempts to remove a participant from this event's participants list.
     *
     * @param username Name of the user to remove.
     * @return Whether removing a participant was successful.
     */
    public boolean leftBy(String username) {
        if (participants != null) {
            Iterator<User> iterator = participants.iterator();
            while (iterator.hasNext()) {
                User participant = iterator.next();
                if (participant.getUsername().compareTo(username) == 0) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the Event's eventDate in a LocalDateTime object form, creates it if necessary.
     *
     * @return LocalDateTime object instance pointing to eventDate.
     */
    public LocalDateTime getEventDateObject() {
        if (eventDateObject == null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            eventDateObject = LocalDateTime.parse(eventDate, dtf);
        }
        return eventDateObject;
    }

    /**
     * Returns the Event's publishedDate in a LocalDateTime object form, creates it if necessary.
     *
     * @return LocalDateTime object instance pointing to publishedDate.
     */
    public LocalDateTime getPublishedDateObject() {
        if (publishedDateObject == null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            publishedDateObject = LocalDateTime.parse(publishedDate, dtf);
        }
        return publishedDateObject;
    }

    /**
     * Checks if username exists within the participants list.
     *
     * @param username Name to check for.
     * @return Whether the user exists on the participants list.
     */
    public boolean containsMember(String username) {
        if (participants != null) {
            for (User participant : participants) {
                if (participant.getUsername().compareTo(username) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public String toString() {
        return "Event " + name + "\nParticipants: " + participants.stream().map(User::getUsername).collect(Collectors.toList());
    }
}
