package com.agh.EventarzGateway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Group {
    private String uuid;
    private String name;
    private String description;
    private String createdDate;
    private int memberCount;
    private int eventCount;
    private boolean stripped;
    public List<User> members;
    public List<Event> events;
    public User founder;
}
