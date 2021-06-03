package com.agh.EventarzGateway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class User {
    private String username;
    private String registerDate;
    private boolean stripped;
    public SecurityDetails securityDetails;
    public List<Event> events;
    public List<Event> organizedEvents;
    public List<Group> groups;
    public List<Group> foundedGroups;
}
