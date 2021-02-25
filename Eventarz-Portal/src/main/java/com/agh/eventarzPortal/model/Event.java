package com.agh.eventarzPortal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
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
@Node("Event")
public class Event {
    @Id
    @GeneratedValue
    @Getter
    private Long id;
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
    private int maxParticipants;
    @Getter
    @Setter
    private String eventDate;
    @Transient
    private LocalDateTime eventDateObject;
    @Getter
    private String publishedDate;
    @Transient
    @Getter
    private LocalDateTime publishedDateObject;
    @Getter
    @Setter
    @Transient
    private boolean expired;

    @Getter
    @Setter
    @Relationship(type = "ORGANIZED", direction = Relationship.Direction.INCOMING)
    public User organizer;
    @Getter
    @Setter
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.Direction.INCOMING)
    public Set<User> participants;
    @Getter
    @Setter
    @Relationship(type = "PUBLISHED_IN", direction = Relationship.Direction.OUTGOING)
    public Group group;

    public static Event of(String name, String description, int maxParticipants, String eventDate, User organizer, Set<User> participants, Group group) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String createdDate = LocalDateTime.now().format(dtf);
        return new Event(null, null, name, description, maxParticipants, eventDate, null, createdDate, null, false, organizer, participants, group);
    }

    //TODO: Exception over return value for failure state?
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

    public LocalDateTime getEventDateObject() {
        if (eventDateObject == null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            eventDateObject = LocalDateTime.parse(eventDate, dtf);
        }
        return eventDateObject;
    }

    public LocalDateTime getPublishedDateObject() {
        if (publishedDateObject == null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            publishedDateObject = LocalDateTime.parse(publishedDate, dtf);
        }
        return publishedDateObject;
    }

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

    public Event withId(Long id) {
        return new Event(id, this.uuid, this.name, this.description, this.maxParticipants, this.eventDate, this.eventDateObject, this.publishedDate, this.publishedDateObject, this.expired, this.organizer, this.participants, this.group);
    }

    public String toString() {
        return "Event " + name + "\nParticipants: " + participants.stream().map(User::getUsername).collect(Collectors.toList());
    }
}
