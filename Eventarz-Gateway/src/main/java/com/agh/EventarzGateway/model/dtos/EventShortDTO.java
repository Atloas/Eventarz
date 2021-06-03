package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: Should this conversion be done here or in DataService? Probably DataService

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDTO {
    private String uuid;
    private String name;
    private String description;
    private int maxParticipants;
    private int participantCount;
    private String eventDate;
    private GroupShortDTO group;

    public EventShortDTO(Event event) {
        this.uuid = event.getUuid();
        this.name = event.getName();
        this.description = event.getDescription();
        this.maxParticipants = event.getMaxParticipants();
        this.participantCount = event.getParticipantCount();
        this.eventDate = event.getEventDate();
    }
}
