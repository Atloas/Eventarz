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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@NodeEntity("Event")
public class Event {
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
    private int maxParticipants;
    @Getter
    @Setter
    private String eventDate;
    @Getter
    private String publishedDate;

    @Getter
    @Setter
    @Relationship(type = "ORGANIZED", direction = Relationship.INCOMING)
    private User organizer;
    @Getter
    @Setter
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.INCOMING)
    private List<User> participants;
    @Getter
    @Setter
    @Relationship(type = "PUBLISHED_IN", direction = Relationship.OUTGOING)
    private Group group;

    //All non-transient arguments constructor used by Neo4j
    @PersistenceConstructor
    public Event(Long id, String uuid, String name, String description, int maxParticipants, String eventDate, String publishedDate, User organizer, List<User> participants, Group group) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.eventDate = eventDate;
        this.publishedDate = publishedDate;
        this.organizer = organizer;
        if (participants == null) {
            this.participants = new ArrayList<>();
        } else {
            this.participants = participants;
        }
        this.group = group;
    }

    public static Event of(String name, String description, int maxParticipants, String eventDate, User organizer, List<User> participants, Group group) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String publishedDate = LocalDateTime.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new Event(null, uuid, name, description, maxParticipants, eventDate, publishedDate, organizer, participants, group);
    }

    //TODO: Exception over return value for failure state?
    public boolean participatedBy(User user) {
        if (participants.size() < maxParticipants) {
            participants.add(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean leftBy(String username) {
        Iterator<User> iterator = participants.iterator();
        while (iterator.hasNext()) {
            User participant = iterator.next();
            if (participant.getUsername().compareTo(username) == 0) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean containsMember(String username) {
        for (User participant : participants) {
            if (participant.getUsername().compareTo(username) == 0) {
                return true;
            }
        }
        return false;
    }

    public EventDTO createDTO() {
        ArrayList<UserDTO> participants = new ArrayList<>();
        for (User participant : this.participants) {
            participants.add(participant.createStrippedDTO());
        }

        return new EventDTO(
                this.id,
                this.uuid,
                this.name,
                this.description,
                this.maxParticipants,
                this.eventDate,
                null,
                this.publishedDate,
                null,
                //TODO: Expiration
                false,
                participants.size(),
                false,
                this.organizer.createStrippedDTO(),
                participants,
                this.group.createStrippedDTO()
        );
    }

    public EventDTO createStrippedDTO() {
        return new EventDTO(
                this.id,
                this.uuid,
                this.name,
                this.description,
                this.maxParticipants,
                this.eventDate,
                null,
                this.publishedDate,
                null,
                //TODO: Expiration
                false,
                this.participants.size(),
                true,
                null,
                new ArrayList<>(),
                null
        );
    }

    public String toString() {
        return "Event " + name;
    }
}
