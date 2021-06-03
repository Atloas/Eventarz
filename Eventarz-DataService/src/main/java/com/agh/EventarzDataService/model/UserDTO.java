package com.agh.EventarzDataService.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO {

    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String registerDate;
    @Getter
    @Setter
    private boolean banned;
    @Getter
    @Setter
    private boolean stripped;

    @Getter
    @Setter
    public SecurityDetailsDTO securityDetails;
    @Getter
    @Setter
    public List<EventDTO> events;
    @Getter
    @Setter
    public List<EventDTO> organizedEvents;
    @Getter
    @Setter
    public List<GroupDTO> groups;
    @Getter
    @Setter
    public List<GroupDTO> foundedGroups;

    @JsonCreator
    public UserDTO(String username, String registerDate, boolean stripped, SecurityDetailsDTO securityDetails, List<EventDTO> events, List<EventDTO> organizedEvents, List<GroupDTO> groups, List<GroupDTO> foundedGroups) {
        this.username = username;
        this.registerDate = registerDate;
        this.stripped = stripped;
        this.securityDetails = securityDetails;
        if (events == null) {
            this.events = new ArrayList<>();
        } else {
            this.events = events;
        }
        if (organizedEvents == null) {
            this.organizedEvents = new ArrayList<>();
        } else {
            this.organizedEvents = organizedEvents;
        }
        if (groups == null) {
            this.groups = new ArrayList<>();
        } else {
            this.groups = groups;
        }
        if (foundedGroups == null) {
            this.foundedGroups = new ArrayList<>();
        } else {
            this.foundedGroups = foundedGroups;
        }
    }

    public String toString() {
        return "User " + username;
    }
}
