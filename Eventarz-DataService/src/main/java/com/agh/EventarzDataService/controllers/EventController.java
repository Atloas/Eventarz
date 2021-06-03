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
package com.agh.EventarzDataService.controllers;

import com.agh.EventarzDataService.EventarzDataServiceApplication;
import com.agh.EventarzDataService.exceptions.EventNotFoundException;
import com.agh.EventarzDataService.exceptions.GroupNotFoundException;
import com.agh.EventarzDataService.exceptions.UserNotFoundException;
import com.agh.EventarzDataService.model.EventDTO;
import com.agh.EventarzDataService.model.EventForm;
import com.agh.EventarzDataService.services.EventService;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

//TODO: handle event expiration

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    private final static Logger log = LoggerFactory.getLogger(EventarzDataServiceApplication.class);

    @GetMapping(value = "/events", params = {"username"})
    @Transactional
    @Retry(name = "getMyEventsRetry")
    public List<EventDTO> getMyEvents(@RequestParam String username) {
        List<EventDTO> eventDTOs = eventService.getMyEvents(username);
        return eventDTOs;
    }

    @GetMapping(value = "/events", params = {"regex"})
    @Transactional
    @Retry(name = "getEventsRegexRetry")
    public List<EventDTO> getEventsRegex(@RequestParam String regex) {
        List<EventDTO> eventDTOs = eventService.getEventsRegex(regex);
        return eventDTOs;
    }

    @PostMapping(value = "/events")
    @Transactional
    @Retry(name = "createEventRetry")
    public EventDTO createEvent(@RequestBody EventForm eventForm) {
        try {
            EventDTO eventDTO = eventService.createEvent(eventForm);
            return eventDTO;
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found!", e);
        } catch (GroupNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found!", e);
        }
    }

    @GetMapping(value = "/events/{uuid}")
    @Transactional
    @Retry(name = "getEventByUuidRetry")
    public EventDTO getEventByUuid(@PathVariable String uuid) {
        try {
            EventDTO eventDTO = eventService.getEventByUuid(uuid);
            return eventDTO;
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found!", e);
        }
    }

    @PutMapping(value = "/events/{uuid}")
    @Transactional
    @Retry(name = "updateEventRetry")
    public EventDTO updateEvent(@PathVariable String uuid, @RequestBody EventForm eventForm) {
        try {
            EventDTO eventDTO = eventService.updateEvent(uuid, eventForm);
            return eventDTO;
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found!", e);
        }
    }

    @DeleteMapping(value = "/events/{uuid}")
    @Transactional
    @Retry(name = "deleteEventRetry")
    public String deleteEvent(@PathVariable String uuid) {
        eventService.deleteEvent(uuid);
        return uuid;
    }

    @PostMapping(value = "/events/{uuid}/participants")
    @Transactional
    @Retry(name = "joinEventRetry")
    public EventDTO joinEvent(@PathVariable String uuid, @RequestBody String username) {
        try {
            EventDTO eventDTO = eventService.joinEvent(uuid, username);
            return eventDTO;
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!", e);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found!", e);
        }
    }

    @DeleteMapping(value = "/events/{uuid}/participants/{username}")
    @Transactional
    @Retry(name = "leaveEventRetry")
    public EventDTO leaveEvent(@PathVariable String uuid, @PathVariable String username) {
        try {
            EventDTO eventDTO = eventService.leaveEvent(uuid, username);
            return eventDTO;
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found!", e);
        }
    }
}
