package com.agh.EventarzDataService.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDTO {
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
    @JsonIgnore
    private LocalDateTime eventDateObject;
    @Getter
    private String publishedDate;
    @JsonIgnore
    private LocalDateTime publishedDateObject;
    @Getter
    @Setter
    private boolean expired;
    @Setter
    private int participantCount;
    @Getter
    @Setter
    private boolean stripped;

    @Getter
    @Setter
    public UserDTO organizer;
    @Getter
    @Setter
    public List<UserDTO> participants;
    @Getter
    @Setter
    public GroupDTO group;

    @JsonCreator
    public EventDTO(Long id, String uuid, String name, String description, int maxParticipants, String eventDate, LocalDateTime eventDateObject, String publishedDate, LocalDateTime publishedDateObject, boolean expired, int participantCount, boolean stripped, UserDTO organizer, List<UserDTO> participants, GroupDTO group) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.eventDate = eventDate;
        this.eventDateObject = eventDateObject;
        this.publishedDate = publishedDate;
        this.publishedDateObject = publishedDateObject;
        //TODO: Expiration
        this.expired = expired;
        this.stripped = stripped;
        if (stripped) {
            this.participantCount = participantCount;
            this.participants = null;
        } else {
            if (participants == null) {
                this.participantCount = 0;
                this.participants = new ArrayList<>();
            } else {
                this.participantCount = participants.size();
                this.participants = participants;
            }
        }
        this.organizer = organizer;
        this.group = group;
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

    public int getParticipantCount() {
        if (stripped) {
            return participantCount;
        } else {
            return participants.size();
        }
    }

    public boolean containsMember(String username) {
        for (UserDTO participant : participants) {
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
