package com.agh.eventarzPortal.web;

import com.agh.eventarzPortal.EventarzPortalApplication;
import com.agh.eventarzPortal.feignClients.DataClient;
import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.Group;
import com.agh.eventarzPortal.model.User;
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
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private DataClient dataClient;

    private final static Logger log = LoggerFactory.getLogger(EventarzPortalApplication.class);

    @Transactional
    @RequestMapping(value = "/admin/user", method = RequestMethod.GET)
    public String adminGetUserByUuid(@RequestParam String username, Model model, Principal principal) {
        User user = dataClient.getUser(username);
        if (user == null) {
            log.error("Requested user not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:/home";
        }
        if (user.getSecurityDetails().getRoles().contains("ADMIN")) {
            model.addAttribute("admin", true);
        }
        model.addAttribute("user", user);
        return "admin/user";
    }

    @Transactional
    @RequestMapping(value = "/admin/findGroup", method = RequestMethod.GET)
    public String adminFindGroup(@RequestParam(required = false) String name, Model model) {
        List<Group> foundGroups = null;
        if (name != null) {
            foundGroups = dataClient.getGroupsByRegex("(?i).*" + name + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundGroups", foundGroups);
        }
        return "admin/findGroup";
    }

    @Transactional
    @RequestMapping(value = "/admin/findEvent", method = RequestMethod.GET)
    public String adminFindEvent(@RequestParam(required = false) String name, Model model) {
        List<Event> foundEvents = null;
        if (name != null) {
            foundEvents = dataClient.getEventsByRegex("(?i).*" + name + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundEvents", foundEvents);
        }
        return "admin/findEvent";
    }

    @Transactional
    @RequestMapping(value = "/admin/findUser", method = RequestMethod.GET)
    public String adminFindUser(@RequestParam(required = false) String username, Model model) {
        List<User> foundUsers = null;
        if (username != null) {
            foundUsers = dataClient.getUsersByRegex("(?i).*" + username + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundUsers", foundUsers);
        }
        return "admin/findUser";
    }

    @Transactional
    @RequestMapping(value = "/admin/deleteUser", method = RequestMethod.POST)
    public String adminDeleteUser(@RequestParam String username, Model model) {
        User user = dataClient.getUser(username);
        dataClient.deleteUser(username);
        model.addAttribute("infoUserDeleted", true);
        return "redirect:/home";
    }

    @Transactional
    @RequestMapping(value = "/admin/deleteEvent", method = RequestMethod.POST)
    public String adminDeleteEvent(@RequestParam String uuid, Model model) {
        dataClient.deleteEvent(uuid);
        model.addAttribute("infoEventDeleted", true);
        return "redirect:/home";
    }

    @Transactional
    @RequestMapping(value = "/admin/deleteGroup", method = RequestMethod.POST)
    public String adminDeleteGroup(@RequestParam String uuid, Model model) {
        dataClient.adminDeleteGroup(uuid);
        model.addAttribute("infoGroupDeleted", true);
        return "redirect:/home";
    }
}
