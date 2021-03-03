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
import com.agh.EventarzDataService.model.EventForm;
import com.agh.EventarzDataService.model.Group;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.repositories.EventRepository;
import com.agh.EventarzDataService.repositories.GroupRepository;
import com.agh.EventarzDataService.repositories.UserRepository;
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

    @Transactional
    @GetMapping(value = "/events")
    public Event getEventByUuid(@RequestParam String uuid) {
        Event event = eventRepository.findByUuid(uuid);
        //TODO: event not found
        return event;
    }

    @Transactional
    @GetMapping(value = "/events/my")
    public List<Event> getMyEvents(@RequestParam String username) {
        List<Event> events = eventRepository.findMyEvents(username);
        events.sort(this::compareEventDates);
        return events;
    }

    @Transactional
    @GetMapping(value = "/events/regex")
    public List<Event> getEventsRegex(@RequestParam String regex) {
        List<Event> events = eventRepository.findByNameRegex(regex);
        return events;
    }

    @Transactional
    @GetMapping(value = "/events/allowedToJoin")
    public boolean checkIfUserAllowedToJoin(@RequestParam String uuid, @RequestParam String username) {
        return eventRepository.checkIfAllowedToJoinEvent(uuid, username);
    }

    @Transactional
    @GetMapping(value = "/events/allowedToPublish")
    public boolean checkIfUserAllowedToPublish(@RequestParam String groupUuid, @RequestParam String username) {
        return eventRepository.checkIfAllowedToPublishEvent(groupUuid, username);
    }

    @Transactional
    @PostMapping(value = "/events")
    public Event createEvent(@RequestBody EventForm eventForm) {
        //Assumes valid eventForm
        User organizer = userRepository.findByUsername(eventForm.getOrganizerUsername());
        Group group = groupRepository.findByUuid(eventForm.getGroupUuid());
        Event newEvent = Event.of(eventForm.getName(), eventForm.getDescription(), eventForm.getMaxParticipants(), eventForm.getEventDate(), organizer, new ArrayList<>(), group);
        if (eventForm.isParticipate()) {
            newEvent.participatedBy(organizer);
        }
        newEvent = eventRepository.save(newEvent);
        return newEvent;
    }

    @Transactional
    @PutMapping(value = "/events/join")
    public Event joinEvent(@RequestParam String uuid, @RequestParam String username) {
        Event event = eventRepository.findByUuid(uuid);
        User user = userRepository.findByUsername(username);
        event.participatedBy(user);
        eventRepository.participatesIn(event.getUuid(), username);
        return event;
    }

    @Transactional
    @PutMapping(value = "/events/leave")
    public Event leaveEvent(@RequestParam String uuid, @RequestParam String username) {
        Event event = eventRepository.findByUuid(uuid);
        if (event.leftBy(username)) {
            eventRepository.leftBy(username, uuid);
        }
        return event;
    }

    @Transactional
    @DeleteMapping(value = "/events")
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
    private int compareEventDates(Event a, Event b) {
        if (a.getEventDateObject().isBefore(b.getEventDateObject())) {
            return -1;
        } else if (b.getEventDateObject().isBefore(a.getEventDateObject())) {
            return 1;
        } else {
            return 0;
        }
    }


}
