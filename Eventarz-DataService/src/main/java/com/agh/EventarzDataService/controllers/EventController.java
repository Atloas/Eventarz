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
import com.agh.EventarzDataService.model.Event;
import com.agh.EventarzDataService.model.EventDTO;
import com.agh.EventarzDataService.model.EventForm;
import com.agh.EventarzDataService.services.EventService;
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

import java.util.List;

//TODO: handle event expiration

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    private final static Logger log = LoggerFactory.getLogger(EventarzDataServiceApplication.class);

    @GetMapping("/test")
    public EventDTO getTestEvent() {
        EventDTO eventDTO = new EventDTO(null, "2", "3", "4", 5, "6", null, "7", null, true, 0, true, null, null, null);
        return eventDTO;
    }

    @GetMapping(value = "/events")
    @Transactional
    @Retry(name = "getEventByUuidRetry")
    public EventDTO getEventByUuid(@RequestParam String uuid) {
        EventDTO eventDTO = eventService.getEventByUuid(uuid);
        return eventDTO;
    }

    @GetMapping(value = "/events/my")
    @Transactional
    @Retry(name = "getMyEventsRetry")
    public List<EventDTO> getMyEvents(@RequestParam String username) {
        List<EventDTO> eventDTOs = eventService.getMyEvents(username);
        return eventDTOs;
    }

    @GetMapping(value = "/events/regex")
    @Transactional
    @Retry(name = "getEventsRegexRetry")
    public List<EventDTO> getEventsRegex(@RequestParam String regex) {
        List<EventDTO> eventDTOs = eventService.getEventsRegex(regex);
        return eventDTOs;
    }

    @GetMapping(value = "/events/allowedToJoin")
    @Transactional
    @Retry(name = "checkIfUserAllowedToJoinRetry")
    public boolean checkIfUserAllowedToJoin(@RequestParam String uuid, @RequestParam String username) {
        boolean allowed = eventService.checkIfUserAllowedToJoin(uuid, username);
        return allowed;
    }

    @GetMapping(value = "/events/allowedToPublish")
    @Transactional
    @Retry(name = "checkIfUserAllowedToPublishRetry")
    public boolean checkIfUserAllowedToPublish(@RequestParam String groupUuid, @RequestParam String username) {
        boolean allowed = eventService.checkIfUserAllowedToPublish(groupUuid, username);
        return allowed;
    }

    @PostMapping(value = "/events")
    @Transactional
    @Retry(name = "createEventRetry")
    public EventDTO createEvent(@RequestBody EventForm eventForm) {
        EventDTO eventDTO = eventService.createEvent(eventForm);
        return eventDTO;
    }

    @PutMapping(value = "/events/join")
    @Transactional
    @Retry(name = "joinEventRetry")
    public EventDTO joinEvent(@RequestParam String uuid, @RequestParam String username) {
        EventDTO eventDTO = eventService.joinEvent(uuid, username);
        return eventDTO;
    }

    @PutMapping(value = "/events/leave")
    @Transactional
    @Retry(name = "leaveEventRetry")
    public EventDTO leaveEvent(@RequestParam String uuid, @RequestParam String username) {
        EventDTO eventDTO = eventService.leaveEvent(uuid, username);
        return eventDTO;
    }

    @DeleteMapping(value = "/events")
    @Transactional
    @Retry(name = "deleteEventRetry")
    public Long deleteEvent(@RequestParam String uuid) {
        Long id = eventService.deleteEvent(uuid);
        return id;
    }
}
