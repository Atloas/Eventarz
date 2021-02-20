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
import com.agh.EventarzEventService.model.Group;
import com.agh.EventarzEventService.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This Controller handles primary User traffic, including login and registration.
 */
@Controller
public class MainController {

    @Autowired
    private EventRepository eventRepository;

    private final static Logger log = LoggerFactory.getLogger(EventarzEventServiceApplication.class);

    /**
     * Displays the details of the specified Event.
     *
     * @param uuid      Identification of the Event to find.
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return The details page of the Event, or redirect to myEvents on failure.
     */
    @Transactional
    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public String getEventByUuid(@RequestParam String uuid, Model model, Principal principal) {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            log.error("Requested event not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:myEvents";
        }
        model.addAttribute("event", event);
        if (eventRepository.checkIfAllowedToJoinEvent(principal.getName(), uuid)) {
            model.addAttribute("allowed", true);
            if (event.containsMember(principal.getName())) {
                model.addAttribute("joined", true);
            }
        }
        if (event.getOrganizer() != null && event.getOrganizer().getUsername().compareTo(principal.getName()) == 0) {
            model.addAttribute("organized", true);
        }
        return "event";
    }

    /**
     * Displays all Events related to the User. Also deletes all Events that are expired.
     *
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return myEvents view.
     */
    @Transactional
    @RequestMapping(value = "/myEvents", method = RequestMethod.GET)
    public String getMyEvents(Model model, Principal principal) {
        LocalDateTime now = LocalDateTime.now();
        Set<Event> events = eventRepository.findMyEvents(principal.getName());
        List<Event> eventsList = new LinkedList<>(events);
        Iterator<Event> eventIterator = eventsList.iterator();
        while (eventIterator.hasNext()) {
            Event event = eventIterator.next();
            Period period = Period.between(now.toLocalDate(), event.getEventDateObject().toLocalDate());
            if (period.getDays() < -1) {
                eventRepository.delete(event);
                eventIterator.remove();
            }
        }
        eventsList.sort(this::compareEventDates);
        model.addAttribute("events", eventsList);
        return "myEvents";
    }

    /**
     * Displays the form for creating an Event and creates the necessary EventForm object.
     * Requires the user to belong to any Group first, otherwise returns an error message.
     *
     * @param model     MVC model.
     * @param principal logged in User.
     * @return createEvent view with an Event creation form or a message.
     */
    @Transactional
    @RequestMapping(value = "/createEvent", method = RequestMethod.GET)
    public String showCreateEvent(Model model, Principal principal) {
        Set<Group> myGroups = groupRepository.findMyGroupNames(principal.getName());
        if (myGroups.size() == 0) {
            model.addAttribute("noGroups", true);
            return "createEvent";
        }
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("myGroups", myGroups);
        return "createEvent";
    }

    /**
     * Handles actual Event creation based on data given from the frontend.
     *
     * @param eventForm EventForm object containing the frontend-provided Event data.
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return The new event's details page view on success, or createEvent view on failure.
     */
    @Transactional
    @RequestMapping(value = "/createEvent", method = RequestMethod.POST)
    public String processCreateEvent(@ModelAttribute EventForm eventForm, Model model, Principal principal) {
        if (!eventForm.validate()) {
            model.addAttribute("errorEventInvalid", true);
            return "createEvent";
        }
        Group group = groupRepository.findByUuid(eventForm.getGroupUuid());
        User organizer = userRepository.findByUsername(principal.getName());
        if (group == null || organizer == null) {
            log.error("Requested user or group not returned from DB!");
            model.addAttribute("errorDb", true);
            return "createEvent";
        }
        Event newEvent = new Event(eventForm.getName(), organizer, group, eventForm.getDescription(), eventForm.getMaxParticipants(), eventForm.getEventDate());
        if (eventForm.isParticipate()) {
            newEvent.participatedBy(organizer);
        }
        newEvent = eventRepository.save(newEvent);
        model.addAttribute("infoEventCreated", true);
        return "redirect:event?uuid=" + newEvent.getUuid();
    }

    /**
     * Adds the current User to the specified Event.
     *
     * @param uuid      The identifier of the desired Event.
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return The details page view of the Event on success or when the Event is full, or the home page on other kind of failure.
     */
    @Transactional
    @RequestMapping(value = "/joinEvent", method = RequestMethod.POST)
    public String joinEvent(@RequestParam String uuid, Model model, Principal principal) {
        if (eventRepository.checkIfAllowedToJoinEvent(principal.getName(), uuid)) {
            Event event = eventRepository.findByUuid(uuid);
            User user = userRepository.findByUsername(principal.getName());
            if (event == null || user == null) {
                log.error("Requested user or event not returned from DB!");
                model.addAttribute("errorDb", true);
                return "redirect:event?uuid=" + uuid;
            }
            if (event.participatedBy(user)) {
                //Workaround due to a Spring Data/Neo4j bug. eventRepository.save(event) wouldn't persist the new relationship.
                eventRepository.participatesIn(event.getUuid(), user.getUsername());
                model.addAttribute("infoEventJoined", true);
            } else {
                model.addAttribute("errorEventFull", true);
            }
            return "redirect:event?uuid=" + uuid;
        }
        log.error("User not allowed to join requested event!");
        return "redirect:event?uuid=" + uuid;
    }

    /**
     * Removes the current User from the specified Event.
     *
     * @param uuid Identifier of the Event.
     * @param model MVC model.
     * @param principal Logged in User.
     * @return The Event's details page view on success, the home page view on failure.
     */
    @Transactional
    @RequestMapping(value = "/leaveEvent", method = RequestMethod.POST)
    public String leaveEvent(@RequestParam String uuid, Model model, Principal principal) {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            log.error("Requested event not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:myEvents";
        }
        if (event.leftBy(principal.getName())) {
            eventRepository.leftBy(principal.getName(), uuid);
            model.addAttribute("infoEventLeft", true);
        }
        return "redirect:event?uuid=" + uuid;
    }

    /**
     * Deletes the specified Event at the request of its creator.
     *
     * @param uuid Identifier of the Event.
     * @param model MVC model.
     * @param principal Logged in User.
     * @return myEvents view.
     */
    @Transactional
    @RequestMapping(value = "/deleteEvent", method = RequestMethod.POST)
    public String deleteEvent(@RequestParam String uuid, Model model, Principal principal) {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            log.error("Requested event not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:myEvents";
        }
        if (event.getOrganizer() != null && event.getOrganizer().getUsername().compareTo(principal.getName()) == 0) {
            eventRepository.delete(event);
            model.addAttribute("infoEventDeleted", true);
        }
        return "redirect:myEvents";
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
