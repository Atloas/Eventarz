package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.exceptions.NotOrganizerException;
import com.agh.EventarzGateway.exceptions.UserNotInEventsGroupException;
import com.agh.EventarzGateway.feignClients.DataClient;
import com.agh.EventarzGateway.model.Event;
import com.agh.EventarzGateway.model.EventForm;
import com.agh.EventarzGateway.model.Group;
import com.agh.EventarzGateway.model.User;
import com.agh.EventarzGateway.model.dtos.EventDTO;
import com.agh.EventarzGateway.model.dtos.EventHomeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private DataClient dataClient;

    public List<EventHomeDTO> getMyEvents(Principal principal) {
        List<Event> events = dataClient.getMyEvents(principal.getName());
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (Event event : events) {
            eventHomeDTOs.add(new EventHomeDTO(event));
        }
        return eventHomeDTOs;
    }

    public List<EventHomeDTO> getHomeEvents(Principal principal) {
        // TODO: Send back only events that are to happen in the next week or so
        List<Event> events = dataClient.getMyEvents(principal.getName());
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (Event event : events) {
            eventHomeDTOs.add(new EventHomeDTO(event));
        }
        return eventHomeDTOs;
    }

    public EventDTO postEvent(EventForm eventForm, Principal principal) throws UserNotInEventsGroupException {
        if (checkIfUserIsInEventsGroup(principal.getName(), eventForm.getGroupUuid())) {
            EventForm completedForm = new EventForm(eventForm);
            completedForm.setOrganizerUsername(principal.getName());
            Event event = dataClient.createEvent(completedForm);
            EventDTO eventDTO = new EventDTO(event);
            return eventDTO;
        } else {
            throw new UserNotInEventsGroupException("User " + principal.getName() + " is not in the target group!");
        }
    }

    public EventDTO getEvent(String uuid, Principal principal) throws UserNotInEventsGroupException {
        Event event = dataClient.getEvent(uuid);
        if (checkIfUserIsInEventsGroup(principal.getName(), event.getGroup().getUuid())) {
            return new EventDTO(event);
        } else {
            throw new UserNotInEventsGroupException("User " + principal.getName() + " is not in the group of event " + uuid + "!");
        }
    }

    public EventDTO updateEvent(String uuid, EventForm eventForm, Principal principal) throws NotOrganizerException {
        Event event = dataClient.getEvent(uuid);
        if (!event.getOrganizer().getUsername().equals(principal.getName())) {
            throw new NotOrganizerException("User " + principal.getName() + " is not the organizer of event " + uuid + "!");
        }
        event = dataClient.updateEvent(uuid, eventForm);
        EventDTO eventDTO = new EventDTO(event);
        return eventDTO;
    }

    public String deleteEvent(String uuid, Principal principal) throws NotOrganizerException {
        Event event = dataClient.getEvent(uuid);
        if (!event.getOrganizer().getUsername().equals(principal.getName())) {
            throw new NotOrganizerException("User " + principal.getName() + " is not the organizer of event " + uuid + " and cannot delete it.");
        }
        return dataClient.deleteEvent(uuid);
    }

    public EventDTO join(String uuid, Principal principal) throws UserNotInEventsGroupException {
        Event event = dataClient.getEvent(uuid);
        if (checkIfUserIsInEventsGroup(principal.getName(), event.getGroup().getUuid())) {
            event = dataClient.joinEvent(uuid, principal.getName());
            return new EventDTO(event);
        } else {
            throw new UserNotInEventsGroupException("User " + principal.getName() + " is not in the group of event " + uuid + "!");
        }
    }

    public EventDTO leave(String uuid, Principal principal) {
        Event event = dataClient.leaveEvent(uuid, principal.getName());
        return new EventDTO(event);
    }

    public List<EventHomeDTO> getEventsByRegex(String name) {
        List<Event> events = dataClient.getEventsByRegex("(?i).*" + name + ".*");
        List<EventHomeDTO> eventHomeDTOs = new ArrayList<>();
        for (Event event : events) {
            eventHomeDTOs.add(new EventHomeDTO(event));
        }
        return eventHomeDTOs;
    }

    public EventDTO adminGetEvent(String uuid) {
        Event event = dataClient.getEvent(uuid);
        return new EventDTO(event);
    }

    public String adminDeleteEvent(String uuid) {
        return dataClient.deleteEvent(uuid);
    }

    private boolean checkIfUserIsInEventsGroup(String username, String groupUuid) {
        Group group = dataClient.getGroup(groupUuid);
        for (User user : group.getMembers()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
