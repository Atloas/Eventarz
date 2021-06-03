package com.agh.EventarzDataService.services;

import com.agh.EventarzDataService.exceptions.EventNotFoundException;
import com.agh.EventarzDataService.exceptions.GroupNotFoundException;
import com.agh.EventarzDataService.exceptions.UserNotFoundException;
import com.agh.EventarzDataService.model.Event;
import com.agh.EventarzDataService.model.EventDTO;
import com.agh.EventarzDataService.model.EventForm;
import com.agh.EventarzDataService.model.Group;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.repositories.EventRepository;
import com.agh.EventarzDataService.repositories.GroupRepository;
import com.agh.EventarzDataService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    public EventDTO getEventByUuid(String uuid) throws EventNotFoundException {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            throw new EventNotFoundException("Event " + uuid + " not found!");
        }
        EventDTO eventDTO = event.createDTO();
        return eventDTO;
    }

    public List<EventDTO> getMyEvents(String username) {
        List<Event> events = eventRepository.findMyEvents(username);
        List<EventDTO> eventDTOs = new ArrayList<>();
        for (Event event : events) {
            eventDTOs.add(event.createDTO());
        }
        eventDTOs.sort(this::compareEventDates);
        return eventDTOs;
    }

    public List<EventDTO> getEventsRegex(String regex) {
        List<Event> events = eventRepository.findByNameRegex(regex);
        List<EventDTO> eventDTOs = new ArrayList<>();
        for (Event event : events) {
            eventDTOs.add(event.createDTO());
        }
        return eventDTOs;
    }

    public EventDTO createEvent(EventForm eventForm) throws UserNotFoundException, GroupNotFoundException {
        User organizer = userRepository.findByUsername(eventForm.getOrganizerUsername());
        if (organizer == null) {
            throw new UserNotFoundException("User " + eventForm.getOrganizerUsername() + " not found!");
        }
        Group group = groupRepository.findByUuid(eventForm.getGroupUuid());
        if (group == null) {
            throw new GroupNotFoundException("Group " + eventForm.getGroupUuid() + " not found!");
        }
        Event event = new Event(eventForm, organizer, group);
        if (eventForm.isParticipate()) {
            event.participatedBy(organizer);
        }
        event = eventRepository.save(event);
        EventDTO eventDTO = event.createDTO();
        return eventDTO;
    }

    public EventDTO updateEvent(String uuid, EventForm eventForm) throws EventNotFoundException {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            throw new EventNotFoundException("Event " + uuid + " not gound!");
        }
        boolean clearParticipants = false;
        if (event.getMaxParticipants() > eventForm.getMaxParticipants()) {
            clearParticipants = true;
        }
        event.setName(eventForm.getName());
        event.setDescription(eventForm.getDescription());
        event.setEventDate(eventForm.getEventDate());
        event.setMaxParticipants(eventForm.getMaxParticipants());
        event = eventRepository.save(event);
        if (clearParticipants) {
            // This needs to happen AFTER save, otherwise it doesn't actually change anything
            eventRepository.dropAllParticipants(uuid);
            event.getParticipants().clear();
        }
        EventDTO eventDTO = event.createDTO();
        return eventDTO;
    }

    public EventDTO joinEvent(String uuid, String username) throws UserNotFoundException, EventNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User " + username + " not found!");
        }
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            throw new EventNotFoundException("Event " + uuid + " not found!");
        }
        event.participatedBy(user);
        eventRepository.join(event.getUuid(), username);
        EventDTO eventDTO = event.createDTO();
        return eventDTO;
    }

    public EventDTO leaveEvent(String uuid, String username) throws EventNotFoundException {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            throw new EventNotFoundException("Event " + uuid + " not found!");
        }
        event.leftBy(username);
        eventRepository.leave(username, uuid);
        EventDTO eventDTO = event.createDTO();
        return eventDTO;
    }

    public String deleteEvent(String uuid) {
        eventRepository.deleteByUuid(uuid);
        return uuid;
    }

    /**
     * A simple unction for comparing LocalTimeDate objects for the purpose of sorting.
     *
     * @param a The first object to compare.
     * @param b The second object to compare.
     * @return -1 if a &lt; b, 0 if a == b, 1 if a &gt; b
     */
    private int compareEventDates(EventDTO a, EventDTO b) {
        if (a.getEventDateObject().isBefore(b.getEventDateObject())) {
            return -1;
        } else if (b.getEventDateObject().isBefore(a.getEventDateObject())) {
            return 1;
        } else {
            return 0;
        }
    }
}
