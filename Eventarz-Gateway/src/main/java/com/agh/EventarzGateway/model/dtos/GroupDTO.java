package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.Event;
import com.agh.EventarzGateway.model.Group;
import com.agh.EventarzGateway.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String uuid;
    private String name;
    private String description;
    private String createdDate;

    public List<UserShortDTO> members;
    public List<EventShortDTO> events;
    public UserShortDTO founder;

    public GroupDTO(Group group) {
        this.uuid = group.getUuid();
        this.name = group.getName();
        this.description = group.getDescription();
        this.createdDate = group.getCreatedDate();

        this.members = new ArrayList<>();
        for (User user: group.getMembers()) {
            this.members.add(new UserShortDTO(user));
        }

        this.events = new ArrayList<>();
        for (Event event: group.getEvents()) {
            this.events.add(new EventShortDTO(event));
        }

        this.founder = new UserShortDTO(group.getFounder());
    }
}
