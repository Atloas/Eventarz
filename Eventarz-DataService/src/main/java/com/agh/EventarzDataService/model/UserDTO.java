package com.agh.EventarzDataService.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    @Getter
    private Long id;
    @Getter
    private String uuid;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String registerDate;
    @Getter
    @Setter
    private boolean stripped;

    @Getter
    @Setter
    public SecurityDetailsDTO securityDetailsDTO;
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
    public UserDTO(Long id, String uuid, String username, String registerDate, boolean stripped, SecurityDetailsDTO securityDetailsDTO, List<EventDTO> events, List<EventDTO> organizedEvents, List<GroupDTO> groups, List<GroupDTO> foundedGroups) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.registerDate = registerDate;
        this.stripped = stripped;
        this.securityDetailsDTO = securityDetailsDTO;
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
