package com.agh.EventarzDataService;

import com.agh.EventarzDataService.model.Event;
import com.agh.EventarzDataService.model.Group;
import com.agh.EventarzDataService.model.SecurityDetails;
import com.agh.EventarzDataService.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SerializerTest {
    private static User user;
    private static Event event;
    private static Group group;

    @BeforeAll
    static void createObjects() {
        user = new User(
                (long) 1,
                "userUuid",
                "username",
                "2021/01/01",
                new SecurityDetails((long) 2, "Password Hash", Arrays.asList("USER")),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        group = new Group(
                (long) 1,
                "groupUuid",
                "Group Name",
                "A test group",
                "2021/01/01",
                new ArrayList<>(),
                new ArrayList<>(),
                null
        );

        event = new Event(
                (long) 4,
                "uuid",
                "Event Name",
                "A test Event",
                5,
                "2021/01/03",
                null,
                "2021/01/01",
                null,
                false,
                null,
                new ArrayList<>(),
                null
        );

        user.getFoundedGroups().add(group);
        user.getGroups().add(group);
        user.getEvents().add(event);
        user.getOrganizedEvents().add(event);

        group.getMembers().add(user);
        group.setFounder(user);
        group.getEvents().add(event);

        event.getParticipants().add(user);
        event.setOrganizer(user);
        event.setGroup(group);
    }

    @Test
    public void serializesEvents() throws IOException {
        String json = new ObjectMapper().writeValueAsString(event);
        Assertions.assertEquals(json, "{\"id\":4,\"uuid\":\"uuid\",\"name\":\"Event Name\",\"description\":\"A test Event\",\"maxParticipants\":5,\"eventDate\":\"2021/01/03\",\"publishedDate\":\"2021/01/01\",\"expired\":false,\"organizer\":{\"id\":1,\"uuid\":\"userUuid\",\"username\":\"username\",\"registerDate\":\"2021/01/01\",\"securityDetails\":{\"id\":2,\"passwordHash\":\"Password Hash\",\"roles\":[\"USER\"]},\"events\":[],\"organizedEvents\":[],\"groups\":[],\"foundedGroups\":[]},\"participants\":[{\"id\":1,\"uuid\":\"userUuid\",\"username\":\"username\",\"registerDate\":\"2021/01/01\",\"securityDetails\":{\"id\":2,\"passwordHash\":\"Password Hash\",\"roles\":[\"USER\"]},\"events\":[],\"organizedEvents\":[],\"groups\":[],\"foundedGroups\":[]}],\"group\":{\"id\":1,\"uuid\":\"groupUuid\",\"name\":\"Group Name\",\"description\":\"A test group\",\"createdDate\":\"2021/01/01\",\"members\":[],\"events\":[],\"founder\":null}}");
    }

    @Test
    void serializesUsers() throws IOException {
        String json = new ObjectMapper().writeValueAsString(user);
        Assertions.assertEquals(json, "{\"id\":1,\"uuid\":\"userUuid\",\"username\":\"username\",\"registerDate\":\"2021/01/01\",\"securityDetails\":{\"id\":2,\"passwordHash\":\"Password Hash\",\"roles\":[\"USER\"]},\"events\":[{\"id\":4,\"uuid\":\"uuid\",\"name\":\"Event Name\",\"description\":\"A test Event\",\"maxParticipants\":5,\"eventDate\":\"2021/01/03\",\"publishedDate\":\"2021/01/01\",\"expired\":false,\"organizer\":null,\"participants\":[],\"group\":null}],\"organizedEvents\":[{\"id\":4,\"uuid\":\"uuid\",\"name\":\"Event Name\",\"description\":\"A test Event\",\"maxParticipants\":5,\"eventDate\":\"2021/01/03\",\"publishedDate\":\"2021/01/01\",\"expired\":false,\"organizer\":null,\"participants\":[],\"group\":null}],\"groups\":[{\"id\":1,\"uuid\":\"groupUuid\",\"name\":\"Group Name\",\"description\":\"A test group\",\"createdDate\":\"2021/01/01\",\"members\":[],\"events\":[],\"founder\":null}],\"foundedGroups\":[{\"id\":1,\"uuid\":\"groupUuid\",\"name\":\"Group Name\",\"description\":\"A test group\",\"createdDate\":\"2021/01/01\",\"members\":[],\"events\":[],\"founder\":null}]}");
    }

    @Test
    void serializesGroups() throws IOException {
        String json = new ObjectMapper().writeValueAsString(group);
        Assertions.assertEquals(json, "{\"id\":1,\"uuid\":\"groupUuid\",\"name\":\"Group Name\",\"description\":\"A test group\",\"createdDate\":\"2021/01/01\",\"members\":[{\"id\":1,\"uuid\":\"userUuid\",\"username\":\"username\",\"registerDate\":\"2021/01/01\",\"securityDetails\":{\"id\":2,\"passwordHash\":\"Password Hash\",\"roles\":[\"USER\"]},\"events\":[],\"organizedEvents\":[],\"groups\":[],\"foundedGroups\":[]}],\"events\":[{\"id\":4,\"uuid\":\"uuid\",\"name\":\"Event Name\",\"description\":\"A test Event\",\"maxParticipants\":5,\"eventDate\":\"2021/01/03\",\"publishedDate\":\"2021/01/01\",\"expired\":false,\"organizer\":null,\"participants\":[],\"group\":null}],\"founder\":{\"id\":1,\"uuid\":\"userUuid\",\"username\":\"username\",\"registerDate\":\"2021/01/01\",\"securityDetails\":{\"id\":2,\"passwordHash\":\"Password Hash\",\"roles\":[\"USER\"]},\"events\":[],\"organizedEvents\":[],\"groups\":[],\"foundedGroups\":[]}}");
    }
}
