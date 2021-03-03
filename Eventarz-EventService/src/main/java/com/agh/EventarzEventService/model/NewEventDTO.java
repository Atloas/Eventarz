package com.agh.EventarzEventService.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewEventDTO {
    EventForm eventForm;
    Group group;
    User organizer;
}
