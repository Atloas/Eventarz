/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agh.EventarzDataService;

import com.agh.EventarzDataService.model.Event;
import com.agh.EventarzDataService.model.EventDTO;
import com.agh.EventarzDataService.model.EventForm;
import com.agh.EventarzDataService.model.Group;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.repositories.EventRepository;
import com.agh.EventarzDataService.repositories.GroupRepository;
import com.agh.EventarzDataService.repositories.UserRepository;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

//TODO: handle event expiration

@RestController
public class EventController {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    private final static Logger log = LoggerFactory.getLogger(EventarzDataServiceApplication.class);

    @GetMapping(value = "/events")
    @Transactional
    @Retry(name = "getEventByUuidRetry")
    public EventDTO getEventByUuid(@RequestParam String uuid) {
        Event event = eventRepository.findByUuid(uuid);
        //TODO: event not found?
        EventDTO eventDTO = event.createDTO();
        return eventDTO;
    }

    @GetMapping(value = "/events/my")
    @Transactional
    @Retry(name = "getMyEventsRetry")
    public List<EventDTO> getMyEvents(@RequestParam String username) {
        List<Event> events = eventRepository.findMyEvents(username);
        List<EventDTO> eventDTOs = new ArrayList<>();
        for (Event event : events) {
            eventDTOs.add(event.createDTO());
        }
        eventDTOs.sort(this::compareEventDates);
        return eventDTOs;
    }

    @GetMapping(value = "/events/regex")
    @Transactional
    @Retry(name = "getEventsRegexRetry")
    public List<EventDTO> getEventsRegex(@RequestParam String regex) {
        List<Event> events = eventRepository.findByNameRegex(regex);
        List<EventDTO> eventDTOs = new ArrayList<>();
        for (Event event : events) {
            eventDTOs.add(event.createDTO());
        }
        return eventDTOs;
    }

    @GetMapping(value = "/events/allowedToJoin")
    @Transactional
    @Retry(name = "checkIfUserAllowedToJoinRetry")
    public boolean checkIfUserAllowedToJoin(@RequestParam String uuid, @RequestParam String username) {
        return eventRepository.checkIfAllowedToJoinEvent(uuid, username);
    }

    @GetMapping(value = "/events/allowedToPublish")
    @Transactional
    @Retry(name = "checkIfUserAllowedToPublishRetry")
    public boolean checkIfUserAllowedToPublish(@RequestParam String groupUuid, @RequestParam String username) {
        return eventRepository.checkIfAllowedToPublishEvent(groupUuid, username);
    }

    @PostMapping(value = "/events")
    @Transactional
    @Retry(name = "createEventRetry")
    public EventDTO createEvent(@RequestBody EventForm eventForm) {
        //Assumes valid eventForm
        User organizer = userRepository.findByUsername(eventForm.getOrganizerUsername());
        Group group = groupRepository.findByUuid(eventForm.getGroupUuid());
        Event newEvent = Event.of(eventForm.getName(), eventForm.getDescription(), eventForm.getMaxParticipants(), eventForm.getEventDate(), organizer, new ArrayList<>(), group);
        if (eventForm.isParticipate()) {
            newEvent.participatedBy(organizer);
        }
        newEvent = eventRepository.save(newEvent);
        EventDTO newEventDTO = newEvent.createDTO();
        return newEventDTO;
    }

    @PutMapping(value = "/events/join")
    @Transactional
    @Retry(name = "joinEventRetry")
    public EventDTO joinEvent(@RequestParam String uuid, @RequestParam String username) {
        Event event = eventRepository.findByUuid(uuid);
        User user = userRepository.findByUsername(username);
        event.participatedBy(user);
        eventRepository.participatesIn(event.getUuid(), username);
        EventDTO eventDTO = event.createDTO();
        return eventDTO;
    }

    @PutMapping(value = "/events/leave")
    @Transactional
    @Retry(name = "leaveEventRetry")
    public EventDTO leaveEvent(@RequestParam String uuid, @RequestParam String username) {
        Event event = eventRepository.findByUuid(uuid);
        if (event.leftBy(username)) {
            eventRepository.leftBy(username, uuid);
        }
        EventDTO eventDTO = event.createDTO();
        return eventDTO;
    }

    @DeleteMapping(value = "/events")
    @Transactional
    @Retry(name = "deleteEventRetry")
    public Long deleteEvent(@RequestParam String uuid) {
        return eventRepository.deleteByUuid(uuid);
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
