package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.Event;
import com.agh.EventarzGateway.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// TODO: Should this conversion be done here or in DataService?

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String uuid;
    private String name;
    private String description;
    private int maxParticipants;
    private String eventDate;
    private String publishedDate;
    private UserShortDTO organizer;
    private List<UserShortDTO> participants;
    private GroupShortDTO group;

    public EventDTO(Event event) {
        this.uuid = event.getUuid();
        this.name = event.getName();
        this.description = event.getDescription();
        this.maxParticipants = event.getMaxParticipants();
        this.eventDate = event.getEventDate();
        this.publishedDate = event.getPublishedDate();
        this.organizer = new UserShortDTO(event.getOrganizer());
        this.participants = new ArrayList<>();
        for (User participant : event.getParticipants()) {
            this.participants.add(new UserShortDTO(participant));
        }
        this.group = new GroupShortDTO(event.getGroup());
    }
}
