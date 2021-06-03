package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventDTO {
    private String uuid;
    private String name;

    public UserEventDTO(Event event) {
        this.uuid = event.getUuid();
        this.name = event.getName();
    }
}
