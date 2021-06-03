package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.Event;
import com.agh.EventarzGateway.model.Group;
import com.agh.EventarzGateway.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String registerDate;
    private boolean banned;
    private List<UserGroupDTO> foundedGroups;
    private List<UserGroupDTO> joinedGroups;
    private List<UserEventDTO> organizedEvents;
    private List<UserEventDTO> joinedEvents;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.registerDate = user.getRegisterDate();
        this.banned = user.getSecurityDetails().isBanned();

        this.foundedGroups = new ArrayList<>();
        for (Group group : user.getFoundedGroups()) {
            this.foundedGroups.add(new UserGroupDTO(group));
        }

        this.joinedGroups = new ArrayList<>();
        for (Group group : user.getGroups()) {
            this.joinedGroups.add(new UserGroupDTO(group));
        }

        this.organizedEvents = new ArrayList<>();
        for (Event event : user.getOrganizedEvents()) {
            this.organizedEvents.add(new UserEventDTO(event));
        }

        this.joinedEvents = new ArrayList<>();
        for (Event event : user.getEvents()) {
            this.joinedEvents.add(new UserEventDTO(event));
        }
    }
}
