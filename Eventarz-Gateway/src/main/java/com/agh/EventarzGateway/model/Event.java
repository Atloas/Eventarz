package com.agh.EventarzGateway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Event {
    private String uuid;
    private String name;
    private String description;
    private int maxParticipants;
    private String eventDate;
    @JsonIgnore
    private LocalDateTime eventDateObject;
    private String publishedDate;
    @JsonIgnore
    private LocalDateTime publishedDateObject;
    private boolean expired;
    private int participantCount;
    private boolean stripped;
    public User organizer;
    public List<User> participants;
    public Group group;

    public LocalDateTime getEventDateObject() {
        if (eventDateObject == null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            eventDateObject = LocalDateTime.parse(eventDate, dtf);
        }
        return eventDateObject;
    }

    public LocalDateTime getPublishedDateObject() {
        if (publishedDateObject == null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            publishedDateObject = LocalDateTime.parse(publishedDate, dtf);
        }
        return publishedDateObject;
    }

    public int getParticipantCount() {
        if (stripped) {
            return participantCount;
        } else {
            return participants.size();
        }
    }

    public boolean containsMember(String username) {
        for (User participant : participants) {
            if (participant.getUsername().compareTo(username) == 0) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "Event " + name;
    }
}
