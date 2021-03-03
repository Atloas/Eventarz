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
import com.agh.eventarzPortal.feignClients.DataClient;
import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.EventForm;
import com.agh.eventarzPortal.model.Group;
import com.agh.eventarzPortal.model.GroupForm;
import com.agh.eventarzPortal.model.User;
import com.agh.eventarzPortal.model.UserForm;
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
import java.util.Iterator;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private DataClient dataClient;
    @Autowired
    private UserService userService;

    private final static Logger log = LoggerFactory.getLogger(EventarzPortalApplication.class);

    @RequestMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("errorLogin", true);
        return "login";
    }


    @RequestMapping("/login-logout")
    public String logout(Model model) {
        model.addAttribute("infoLogout", true);
        return "login";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistration(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "registration";
    }

    @Transactional
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String processRegistration(@ModelAttribute("userForm") UserForm userForm, Model model) {
        if (!userForm.validate()) {
            model.addAttribute("errorUserInvalid", true);
            return "registration";
        }
        try {
            User user = userService.registerNewUserAccount(userForm);
        } catch (UserAlreadyExistsException uaeEx) {
            model.addAttribute("errorUserExists", true);
            return "registration";
        }
        model.addAttribute("infoRegistered", true);
        return "login";
    }

    @Transactional
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public String getGroupByUuid(@RequestParam String uuid, Model model, Principal principal) {
        Group group = dataClient.getGroup(uuid);
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

    @Transactional
    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public String getEventByUuid(@RequestParam String uuid, Model model, Principal principal) {
        Event event = dataClient.getEvent(uuid);
        if (event == null) {
            log.error("Requested event not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:myEvents";
        }
        model.addAttribute("event", event);
        if (dataClient.checkIfAllowedToJoinEvent(uuid, principal.getName())) {
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

    @Transactional
    @RequestMapping(value = "/myEvents", method = RequestMethod.GET)
    public String getMyEvents(Model model, Principal principal) {
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = dataClient.getMyEvents(principal.getName());
        Iterator<Event> eventIterator = events.iterator();
        while (eventIterator.hasNext()) {
            Event event = eventIterator.next();
            Period period = Period.between(now.toLocalDate(), event.getEventDateObject().toLocalDate());
            if (period.getDays() < -1) {
                dataClient.deleteEvent(event.getUuid());
                eventIterator.remove();
            }
        }
        events.sort(this::compareEventDates);
        model.addAttribute("events", events);
        return "myEvents";
    }

    @Transactional
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(Model model, Principal principal) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("username", principal.getName());
        model.addAttribute("serverTime", now.format(dtf));

        List<Event> events = dataClient.getMyEvents(principal.getName());
        List<Event> upcomingEvents = new ArrayList<>();
        Iterator<Event> eventIterator = events.iterator();
        while (eventIterator.hasNext()) {
            Event event = eventIterator.next();
            Period period = Period.between(now.toLocalDate(), event.getEventDateObject().toLocalDate());
            if (period.getDays() < -1) {
                dataClient.deleteEvent(event.getUuid());
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

    @Transactional
    @RequestMapping(value = "/myGroups", method = RequestMethod.GET)
    public String getMyGroups(Model model, Principal principal) {
        List<Group> groups = dataClient.getMyGroups(principal.getName());
        model.addAttribute("groups", groups);
        return "myGroups";
    }

    @Transactional
    @RequestMapping(value = "/createEvent", method = RequestMethod.GET)
    public String showCreateEvent(Model model, Principal principal) {
        List<Group> myGroups = dataClient.getMyGroups(principal.getName());
        if (myGroups.size() == 0) {
            model.addAttribute("noGroups", true);
            return "createEvent";
        }
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("myGroups", myGroups);
        return "createEvent";
    }

    @Transactional
    @RequestMapping(value = "/createEvent", method = RequestMethod.POST)
    public String processCreateEvent(@ModelAttribute EventForm eventForm, Model model, Principal principal) {
        eventForm.setOrganizerUsername(principal.getName());
        if (!eventForm.validate()) {
            model.addAttribute("errorEventInvalid", true);
            return "createEvent";
        }
        boolean allowed = dataClient.checkIfAllowedToPublishEvent(eventForm.getGroupUuid(), principal.getName());
        if (allowed) {
            Event newEvent = dataClient.createEvent(eventForm);
            model.addAttribute("infoEventCreated", true);
            return "redirect:event?uuid=" + newEvent.getUuid();
        } else {
            //TODO: Error message
            return "redirect:myEvents";
        }
    }

    @RequestMapping(value = "/createGroup", method = RequestMethod.GET)
    public String showCreateGroup(Model model) {
        model.addAttribute("groupForm", new GroupForm());
        return "createGroup";
    }

    @Transactional
    @RequestMapping(value = "/createGroup", method = RequestMethod.POST)
    public String processCreateGroup(@ModelAttribute GroupForm groupForm, Model model, Principal principal) {
        groupForm.setFounderUsername(principal.getName());
        if (!groupForm.validate()) {
            model.addAttribute("errorGroupInvalid", true);
            return "createGroup";
        }
        Group group = dataClient.createGroup(groupForm);
        model.addAttribute("infoGroupCreated", true);
        return "redirect:group?uuid=" + group.getUuid();
    }

    @Transactional
    @RequestMapping(value = "/findGroup", method = RequestMethod.GET)
    public String findGroup(@RequestParam(required = false) String name, Model model) {
        List<Group> foundGroups = null;
        if (name != null) {
            foundGroups = dataClient.getGroupsByRegex("(?i).*" + name + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundGroups", foundGroups);
        }
        return "findGroup";
    }

    @Transactional
    @RequestMapping(value = "/joinGroup", method = RequestMethod.POST)
    public String joinGroup(@RequestParam String uuid, Model model, Principal principal) {
        dataClient.joinGroup(uuid, principal.getName());
        return "redirect:group?uuid=" + uuid;
    }

    @Transactional
    @RequestMapping(value = "/joinEvent", method = RequestMethod.POST)
    public String joinEvent(@RequestParam String uuid, Model model, Principal principal) {
        boolean allowed = dataClient.checkIfAllowedToJoinEvent(uuid, principal.getName());
        if (allowed) {
            dataClient.joinEvent(uuid, principal.getName());
            return "redirect:event?uuid=" + uuid;
        } else {
            //TODO: Error message?
            return "redirect:myEvents";
        }
    }

    @Transactional
    @RequestMapping(value = "/leaveGroup", method = RequestMethod.POST)
    public String leaveGroup(@RequestParam String uuid, Model model, Principal principal) {
        dataClient.leaveGroup(uuid, principal.getName());
        return "redirect:group?uuid=" + uuid;
    }

    @Transactional
    @RequestMapping(value = "/leaveEvent", method = RequestMethod.POST)
    public String leaveEvent(@RequestParam String uuid, Model model, Principal principal) {
        dataClient.leaveEvent(uuid, principal.getName());
        model.addAttribute("infoEventLeft", true);
        return "redirect:event?uuid=" + uuid;
    }

    @Transactional
    @RequestMapping(value = "/deleteGroup", method = RequestMethod.POST)
    public String deleteGroup(@RequestParam String uuid, Model model, Principal principal) {
        dataClient.deleteGroup(uuid, principal.getName());
        //TODO: Error handling
        model.addAttribute("infoGroupDeleted", true);
        return "redirect:myGroups";
    }

    @Transactional
    @RequestMapping(value = "/deleteEvent", method = RequestMethod.POST)
    public String deleteEvent(@RequestParam String uuid, Model model, Principal principal) {
        dataClient.deleteEvent(uuid);
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
