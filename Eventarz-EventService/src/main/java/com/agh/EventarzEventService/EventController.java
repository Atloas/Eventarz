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
package com.agh.EventarzEventService;

import com.agh.EventarzEventService.model.Event;
import com.agh.EventarzEventService.model.EventForm;
import com.agh.EventarzEventService.model.NewEventDTO;
import com.agh.EventarzEventService.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//TODO: handle event expiration

@RestController
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    private final static Logger log = LoggerFactory.getLogger(EventarzEventServiceApplication.class);

    @Transactional
    @GetMapping(value = "/event")
    public Event getEventByUuid(@RequestParam String uuid) {
        Event event = eventRepository.findByUuid(uuid);
        //TODO: event not found
        return event;
    }

    @Transactional
    @GetMapping(value = "/myEvents")
    public List<Event> getMyEvents(@RequestParam String username) {
        Set<Event> events = eventRepository.findMyEvents(username);
        List<Event> eventsList = new LinkedList<>(events);
        eventsList.sort(this::compareEventDates);
        return eventsList;
    }

    @Transactional
    @PostMapping(value = "/event")
    public Event createEvent(@RequestBody NewEventDTO newEventDTO) {
        //Assumes valid eventForm
        EventForm eventForm = newEventDTO.getEventForm();
        Event newEvent = Event.of(eventForm.getName(), newEventDTO.getOrganizer(), newEventDTO.getGroup(), eventForm.getDescription(), eventForm.getMaxParticipants(), eventForm.getEventDate());
        if (eventForm.isParticipate()) {
            newEvent.participatedBy(newEventDTO.getOrganizer());
        }
        newEvent = eventRepository.save(newEvent);
        return newEvent;
    }

    @Transactional
    @PutMapping(value = "/joinEvent")
    public Event joinEvent(@RequestParam String eventUUID, @RequestBody User user) {
        Event event = eventRepository.findByUuid(eventUUID);
        if (event.participatedBy(user)) {
            //Workaround due to a Spring Data/Neo4j bug. eventRepository.save(event) wouldn't persist the new relationship.
            eventRepository.participatesIn(event.getUuid(), user.getUsername());
        }
        return event;
    }

    @Transactional
    @PutMapping(value = "/leaveEvent")
    public Event leaveEvent(@RequestParam String eventUUID, @RequestBody User user) {
        Event event = eventRepository.findByUuid(eventUUID);
        if (event.leftBy(user.getUsername())) {
            eventRepository.leftBy(user.getUsername(), eventUUID);
        }
        return event;
    }

    @Transactional
    @DeleteMapping(value = "/deleteEvent")
    public void deleteEvent(@RequestParam String eventUUID) {
        eventRepository.deleteByUuid(eventUUID);
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
