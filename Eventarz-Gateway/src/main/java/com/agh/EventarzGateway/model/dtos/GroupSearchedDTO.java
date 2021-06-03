package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSearchedDTO {
    private String uuid;
    private String name;
    private String description;
    private int memberCount;
    private int eventCount;

    public GroupSearchedDTO(Group group) {
        this.uuid = group.getUuid();
        this.name = group.getName();
        this.description = group.getDescription();
        this.memberCount = group.getMemberCount();
        this.eventCount = group.getEventCount();
    }
}
