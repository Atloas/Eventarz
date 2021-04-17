package com.agh.EventarzDataService.services;

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
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    public EventDTO getEventByUuid(String uuid) {
        Optional<Event> event = eventRepository.findByUuid(uuid);
        EventDTO eventDTO = null;
        if (event.isPresent()) {
            eventDTO = event.get().createDTO();
        }
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

    public boolean checkIfUserAllowedToJoin(String uuid, String username) {
        return eventRepository.checkIfAllowedToJoinEvent(uuid, username);
    }

    public boolean checkIfUserAllowedToPublish(String groupUuid, String username) {
        return eventRepository.checkIfAllowedToPublishEvent(groupUuid, username);
    }

    public EventDTO createEvent(EventForm eventForm) {
        //Assumes valid eventForm
        Optional<User> organizer = userRepository.findByUsername(eventForm.getOrganizerUsername());
        Optional<Group> group = groupRepository.findByUuid(eventForm.getGroupUuid());
        if (organizer.isPresent() && group.isPresent()) {
            Event newEvent = null;
            newEvent = Event.of(eventForm.getName(), eventForm.getDescription(), eventForm.getMaxParticipants(), eventForm.getEventDate(), organizer.get(), new ArrayList<>(), group.get());
            if (eventForm.isParticipate()) {
                newEvent.participatedBy(organizer.get());
            }
            newEvent = eventRepository.save(newEvent);
            return newEvent.createDTO();
        } else {
            //TODO: Some error handling or reporting?
            return null;
        }
    }

    public EventDTO joinEvent(String uuid, String username) {
        Optional<Event> event = eventRepository.findByUuid(uuid);
        Optional<User> user = userRepository.findByUsername(username);
        EventDTO eventDTO = null;
        if (event.isPresent() && user.isPresent()) {
            event.get().participatedBy(user.get());
            eventRepository.participatesIn(event.get().getUuid(), username);
            eventDTO = event.get().createDTO();
        }
        return eventDTO;
    }

    public EventDTO leaveEvent(String uuid, String username) {
        Optional<Event> event = eventRepository.findByUuid(uuid);
        EventDTO eventDTO = null;
        if (event.isPresent()) {
            if (event.get().leftBy(username)) {
                eventRepository.leftBy(username, uuid);
            }
            eventDTO = event.get().createDTO();
        }
        return eventDTO;
    }

    public Long deleteEvent(String uuid) {
        return eventRepository.deleteByUuid(uuid).orElse(null);
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
