package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupDTO {
    private String uuid;
    private String name;

    public UserGroupDTO(Group group) {
        this.uuid = group.getUuid();
        this.name = group.getName();
    }
}
