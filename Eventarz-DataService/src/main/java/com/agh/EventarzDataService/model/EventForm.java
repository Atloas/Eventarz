package com.agh.EventarzDataService.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class EventForm {
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
    @Setter
    private boolean participate;
    @Getter
    @Setter
    private String groupUuid;

    public EventForm() {
        maxParticipants = 1;
    }

    private void convertEventDate() {
        eventDate = eventDate.replace('-', '/').replace('T', ' ');
    }

    public boolean validate() {
        //name
        if (name.length() < 5 || Pattern.matches(".*[^a-zA-Z0-9\\s-:()\u0104\u0106\u0118\u0141\u0143\u00D3\u015A\u0179\u017B\u0105\u0107\u0119\u0142\u0144\u00F3\u015B\u017A\u017C.,!?$]+.*", name)) {
            return false;
        }
        //description
        if (Pattern.matches(".*[^a-zA-Z0-9\\s-:()\u0104\u0106\u0118\u0141\u0143\u00D3\u015A\u0179\u017B\u0105\u0107\u0119\u0142\u0144\u00F3\u015B\u017A\u017C.,!?$]+.*", description)) {
            return false;
        }
        //maxParticipants
        if (maxParticipants < 1) {
            return false;
        }
        //eventDate
        convertEventDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime eventDateObject = LocalDateTime.parse(eventDate, dtf);
        //TODO: Ideally give this like 1 minute of leeway, so it's possible to set events to right now
        if (eventDateObject.isBefore(LocalDateTime.now())) {
            return false;
        }
        //groupUuid
        if (groupUuid == null) {
            return false;
        }

        return true;
    }
}
