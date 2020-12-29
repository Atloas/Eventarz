package com.agh.eventarzPortal.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * This class is used to hold data about a soon-to-be-created Event, given by the frontend. Also contains validation logic.
 */
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
    /**
     * Whether the user creating this event wishes to also participate in it.
     */
    @Getter
    @Setter
    private boolean participate;
    /**
     * Contains the unique identifier of the group this event was published in.
     */
    @Getter
    @Setter
    private String groupUuid;

    /**
     * Setting maxParticipants to 1 here helps enforce the minimum of 1 for this value on the front end.
     */
    public EventForm() {
        maxParticipants = 1;
    }

    /**
     * Converts this object's eventDate from the given YYYY-MM-DDTHH:mm format to YYYY/MM/DD HH:mm, which is used in the rest of this program.
     */
    private void convertEventDate() {
        eventDate = eventDate.replace('-', '/').replace('T', ' ');
    }

    /**
     * Validates the provided data.
     *
     * @return Whether the data is valid or not.
     */
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
