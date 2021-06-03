package com.agh.EventarzGateway.controllers;

import com.agh.EventarzGateway.exceptions.NotOrganizerException;
import com.agh.EventarzGateway.exceptions.UserNotInEventsGroupException;
import com.agh.EventarzGateway.model.EventForm;
import com.agh.EventarzGateway.model.dtos.EventDTO;
import com.agh.EventarzGateway.model.dtos.EventHomeDTO;
import com.agh.EventarzGateway.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Secured("USER")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/events")
    public List<EventHomeDTO> getMy(Principal principal) {
        List<EventHomeDTO> eventHomeDTOs = eventService.getMyEvents(principal);
        return eventHomeDTOs;
    }

    @GetMapping(value = "/events", params = {"home"})
    public List<EventHomeDTO> getHome(Principal principal) {
        List<EventHomeDTO> eventHomeDTOs = eventService.getHomeEvents(principal);
        return eventHomeDTOs;
    }

    @PostMapping("/events")
    public EventDTO post(@Valid @RequestBody EventForm eventForm, Principal principal) {
        try {
            EventDTO eventDTO = eventService.postEvent(eventForm, principal);
            return eventDTO;
        } catch (UserNotInEventsGroupException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to post to this Group!", e);
        }
    }

    @GetMapping("/events/{uuid}")
    public EventDTO get(@PathVariable String uuid, Principal principal) {
        try {
            EventDTO eventDTO = eventService.getEvent(uuid, principal);
            return eventDTO;
        } catch (UserNotInEventsGroupException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view this Event!", e);
        }
    }

    @PutMapping("/events/{uuid}")
    public EventDTO update(@Valid @RequestBody EventForm eventForm, @PathVariable String uuid, Principal principal) {
        try {
            EventDTO eventDTO = eventService.updateEvent(uuid, eventForm, principal);
            return eventDTO;
        } catch (NotOrganizerException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this Event!", e);
        }
    }

    @DeleteMapping("/events/{uuid}")
    public String delete(@PathVariable String uuid, Principal principal) {
        try {
            String oldUuid = eventService.deleteEvent(uuid, principal);
            return oldUuid;
        } catch (NotOrganizerException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this Event!", e);
        }
    }

    @PostMapping("/events/{uuid}/participants")
    public EventDTO join(@PathVariable String uuid, Principal principal) {
        try {
            EventDTO eventDTO = eventService.join(uuid, principal);
            return eventDTO;
        } catch (UserNotInEventsGroupException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to join this Event!", e);
        }
    }

    // username is unused
    @DeleteMapping("/events/{uuid}/participants/{username}")
    public EventDTO leave(@PathVariable String uuid, @PathVariable String username, Principal principal) {
        EventDTO eventDTO = eventService.leave(uuid, principal);
        return eventDTO;
    }
}
