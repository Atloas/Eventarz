package com.agh.eventarzPortal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewEventDTO {
    EventForm eventForm;
    Group group;
    User organizer;
}
