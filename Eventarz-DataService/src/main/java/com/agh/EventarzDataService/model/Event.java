package com.agh.EventarzDataService.model;

import com.agh.EventarzDataService.model.serializers.EventSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@NodeEntity("Event")
@JsonSerialize(using = EventSerializer.class)
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
    @Transient
    @JsonIgnore
    @Getter
    private LocalDateTime eventDateObject;
    @Getter
    private String publishedDate;
    @Transient
    @JsonIgnore
    @Getter
    private LocalDateTime publishedDateObject;
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
    public List<User> participants;
    @Getter
    @Setter
    @Relationship(type = "PUBLISHED_IN", direction = Relationship.OUTGOING)
    public Group group;

    public Event(Long id, String uuid, String name, String description, int maxParticipants, String eventDate, String publishedDate, User organizer, List<User> participants, Group group) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.eventDate = eventDate;
        this.publishedDate = publishedDate;
        this.organizer = organizer;
        this.participants = participants;
        this.group = group;
    }

//    public Event(Event that) {
//        this(that.id, that.uuid, that.name, that.description, that.maxParticipants, that.eventDate, that.eventDateObject,
//                that.publishedDate, that.publishedDateObject, that.expired, that.organizer, that.participants, that.group);
//    }

    public static Event of(String name, String description, int maxParticipants, String eventDate, User organizer, List<User> participants, Group group) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String publishedDate = LocalDateTime.now().format(dtf);
        String uuid = UUID.randomUUID().toString();
        return new Event(null, uuid, name, description, maxParticipants, eventDate, publishedDate, organizer, participants, group);
    }

    //TODO: Exception over return value for failure state?
    public boolean participatedBy(User user) {
        if (participants == null) {
            participants = new ArrayList<>();
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
        return new Event(id, this.uuid, this.name, this.description, this.maxParticipants, this.eventDate, this.publishedDate, this.organizer, this.participants, this.group);
    }

    public Event createStrippedCopy() {
        Event copy = new Event(this.id, this.uuid, this.name, this.description, this.maxParticipants, this.eventDate, null,
                this.publishedDate, null, this.expired, null, null, null);
        return copy;
    }

    public String toString() {
        return "Event " + name;
    }
}
