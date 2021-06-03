package com.agh.EventarzAuthenticationService.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class GroupDTO {
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
    private String createdDate;
    @Getter
    @Setter
    private boolean stripped;

    @Getter
    @Setter
    public List<UserDTO> members;
    @Getter
    @Setter
    public List<EventDTO> events;
    @Getter
    @Setter
    public UserDTO founder;

    @JsonCreator
    public GroupDTO(Long id, String uuid, String name, String description, String createdDate, boolean stripped, List<UserDTO> members, List<EventDTO> events, UserDTO founder) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.stripped = stripped;
        if (stripped) {
            this.members = null;
            this.events = null;
            this.founder = null;
        } else {
            if (members == null) {
                this.members = new ArrayList<>();
            } else {
                this.members = members;
            }
            if (events == null) {
                this.events = new ArrayList<>();
            } else {
                this.events = events;
            }
            this.founder = founder;
        }
    }

    public boolean containsMember(String username) {
        if (members != null) {
            for (UserDTO member : members) {
                if (member.getUsername().compareTo(username) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public String toString() {
        return "Group " + name;
    }
}
