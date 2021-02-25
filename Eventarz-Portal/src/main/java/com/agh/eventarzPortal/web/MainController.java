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
package com.agh.eventarzPortal.web;

import com.agh.eventarzPortal.EventarzPortalApplication;
import com.agh.eventarzPortal.UserAlreadyExistsException;
import com.agh.eventarzPortal.UserService;
import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.EventForm;
import com.agh.eventarzPortal.model.Group;
import com.agh.eventarzPortal.model.GroupForm;
import com.agh.eventarzPortal.model.User;
import com.agh.eventarzPortal.model.UserForm;
import com.agh.eventarzPortal.repositories.EventRepository;
import com.agh.eventarzPortal.repositories.GroupRepository;
import com.agh.eventarzPortal.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
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
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserService userService;

    private final static Logger log = LoggerFactory.getLogger(EventarzPortalApplication.class);

    /**
     * A simple redirect to the home page.
     *
     * @return a redirect to /home
     */
    @RequestMapping("/")
    public String root() {
        return "redirect:/home";
    }

    /**
     * Returns a view containing the login page.
     *
     * @return login view.
     */
    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    /**
     * Redirect destination when there is a login error. The view displays a message.
     *
     * @param model MVC model.
     * @return login view.
     */
    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("errorLogin", true);
        return "login";
    }

    /**
     * Redirect destination when the User logs out. The view displays a message.
     *
     * @param model MVC model.
     * @return login view.
     */
    @RequestMapping("/login-logout")
    public String logout(Model model) {
        model.addAttribute("infoLogout", true);
        return "login";
    }

    /**
     * Returns a view containing the registration page.
     *
     * @param model MVC model.
     * @return registration view.
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistration(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "registration";
    }

    /**
     * Handles User registration and data validation to that end.
     *
     * @param userForm A UserForm object containing frontend-provided User data.
     * @param model    MVC model.
     * @return registration view on failure, login on success.
     */
    @Transactional
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String processRegistration(@ModelAttribute("userForm") UserForm userForm, Model model) {
        if (!userForm.validate()) {
            model.addAttribute("errorUserInvalid", true);
            return "registration";
        }
        try {
            User registered = userService.registerNewUserAccount(userForm);
        } catch (UserAlreadyExistsException uaeEx) {
            model.addAttribute("errorUserExists", true);
            return "registration";
        }
        model.addAttribute("infoRegistered", true);
        return "login";
    }

    /**
     * Displays the details of the specified Group.
     *
     * @param uuid      Identification of the Group to find.
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return The details page of the Group, or redirect to myGroups on failure.
     */
    @Transactional
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public String getGroupByUuid(@RequestParam String uuid, Model model, Principal principal) {
        Group group = groupRepository.findByUuid(uuid, 2);
        if (group == null) {
            log.error("Requested group not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:myGroups";
        }
        if (group.containsMember(principal.getName())) {
            model.addAttribute("joined", true);
        }
        if (group.getFounder() != null && group.getFounder().getUsername().compareTo(principal.getName()) == 0) {
            model.addAttribute("founded", true);
        }
        model.addAttribute("group", group);
        return "group";
    }

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
     * A home page. Displays all events related to the logged in User that are to happen today or tomorrow.
     * Also deletes all Events that are expired.
     *
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return home view.
     */
    @Transactional
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(Model model, Principal principal) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("username", principal.getName());
        model.addAttribute("serverTime", now.format(dtf));

        Set<Event> events = eventRepository.findMyEvents(principal.getName());
        List<Event> upcomingEvents = new ArrayList<>();
        Iterator<Event> eventIterator = events.iterator();
        while (eventIterator.hasNext()) {
            Event event = eventIterator.next();
            Period period = Period.between(now.toLocalDate(), event.getEventDateObject().toLocalDate());
            if (period.getDays() < -1) {
                eventRepository.delete(event);
                eventIterator.remove();
                continue;
            }
            if (period.getDays() < 2 && event.getEventDateObject().isAfter(now))
                upcomingEvents.add(event);
        }
        upcomingEvents.sort(this::compareEventDates);

        model.addAttribute("upcomingEvents", upcomingEvents);
        return "home";
    }

    /**
     * Displays all Groups related to the User.
     *
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return myGroups view.
     */
    @Transactional
    @RequestMapping(value = "/myGroups", method = RequestMethod.GET)
    public String getMyGroups(Model model, Principal principal) {
        Set<Group> groups = groupRepository.findMyGroups(principal.getName());
        model.addAttribute("groups", groups);
        return "myGroups";
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
        Event newEvent = Event.of(eventForm.getName(), eventForm.getDescription(), eventForm.getMaxParticipants(), eventForm.getEventDate(), organizer, new HashSet<>(), group);
        if (eventForm.isParticipate()) {
            newEvent.participatedBy(organizer);
        }
        newEvent = eventRepository.save(newEvent);
        model.addAttribute("infoEventCreated", true);
        return "redirect:event?uuid=" + newEvent.getUuid();
    }

    /**
     * Displays the form for creating a Group and creates the necessary GroupForm object.
     *
     * @param model MVC model.
     * @return createGroup view with a Group creation form or a message.
     */
    @RequestMapping(value = "/createGroup", method = RequestMethod.GET)
    public String showCreateGroup(Model model) {
        model.addAttribute("groupForm", new GroupForm());
        return "createGroup";
    }

    /**
     * Handles actual Group creation based on data given from the frontend.
     *
     * @param groupForm GroupForm object containing the frontend-provided Group data.
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return The new group's details page view on success, or createGroup view on failure.
     */
    @Transactional
    @RequestMapping(value = "/createGroup", method = RequestMethod.POST)
    public String processCreateGroup(@ModelAttribute GroupForm groupForm, Model model, Principal principal) {
        if (!groupForm.validate()) {
            model.addAttribute("errorGroupInvalid", true);
            return "createGroup";
        }
        User founder = userRepository.findByUsername(principal.getName());
        if (founder == null) {
            log.error("Requested user not returned from DB!");
            model.addAttribute("errorDb", true);
            return "createGroup";
        }
        Group newGroup = Group.of(groupForm.getName(), groupForm.getDescription(), new HashSet<>(), new HashSet<>(), founder);
        newGroup.joinedBy(founder);
        newGroup = groupRepository.save(newGroup);
        model.addAttribute("infoGroupCreated", true);
        return "redirect:group?uuid=" + newGroup.getUuid();
    }

    /**
     * Returns a view allowing to search the database for Groups, or containing the search results if name is provided.
     *
     * @param name  Optional, name to search for.
     * @param model MVC model.
     * @return /findGroup view.
     */
    @Transactional
    @RequestMapping(value = "/findGroup", method = RequestMethod.GET)
    public String findGroup(@RequestParam(required = false) String name, Model model) {
        Set<Group> foundGroups = null;
        if (name != null) {
            foundGroups = groupRepository.findByNameRegex("(?i).*" + name + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundGroups", foundGroups);
        }
        return "findGroup";
    }

    /**
     * Adds the current User to the specified Group.
     *
     * @param uuid      The identifier of the desired Group.
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return The details page view of the Group on success, or the home page on failure.
     */
    @Transactional
    @RequestMapping(value = "/joinGroup", method = RequestMethod.POST)
    public String joinGroup(@RequestParam String uuid, Model model, Principal principal) {
        Group group = groupRepository.findByUuid(uuid);
        User user = userRepository.findByUsername(principal.getName());
        if (group == null || user == null) {
            log.error("Requested user or group not returned from DB!");
            model.addAttribute("ebError", true);
            return "redirect:home";
        }
        group.joinedBy(user);
        //Workaround due to a Spring Data/Neo4j bug. groupRepository.save(group) wouldn't persist the new relationship.
        groupRepository.belongsTo(group.getUuid(), user.getUsername());
        model.addAttribute("infoGroupJoined", true);
        return "redirect:group?uuid=" + uuid;
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
     * Removes the current User from the specified Group.
     *
     * @param uuid Identifier of the Group.
     * @param model MVC model.
     * @param principal Logged in User.
     * @return The Group's details page view on success, the home page view on failure.
     */
    @Transactional
    @RequestMapping(value = "/leaveGroup", method = RequestMethod.POST)
    public String leaveGroup(@RequestParam String uuid, Model model, Principal principal) {
        Group group = groupRepository.findByUuid(uuid);
        if (group == null) {
            log.error("Requested group not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:myGroups";
        }
        if (group.leftBy(principal.getName())) {
            groupRepository.leftBy(principal.getName(), uuid);
            model.addAttribute("infoGroupLeft", true);
        }
        return "redirect:group?uuid=" + uuid;
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
     * Deletes the specified Group at the request of its creator.
     *
     * @param uuid Identifier of the Group.
     * @param model MVC model.
     * @param principal Logged in User.
     * @return myGroups view.
     */
    @Transactional
    @RequestMapping(value = "/deleteGroup", method = RequestMethod.POST)
    public String deleteGroup(@RequestParam String uuid, Model model, Principal principal) {
        Group group = groupRepository.findByUuid(uuid);
        if (group == null) {
            log.error("Requested group not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:myGroups";
        }
        if (group.getFounder() != null && group.getFounder().getUsername().compareTo(principal.getName()) == 0) {
            groupRepository.deleteWithEvents(group.getUuid());
            model.addAttribute("infoGroupDeleted", true);
        }
        return "redirect:myGroups";
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
