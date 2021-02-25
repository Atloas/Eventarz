package com.agh.eventarzPortal.model;

import lombok.Getter;
import lombok.Setter;

public class NewEventDTO {
    @Getter
    @Setter
    EventForm eventForm;

    @Getter
    @Setter
    Group group;

    @Getter
    @Setter
    User organizer;
}
