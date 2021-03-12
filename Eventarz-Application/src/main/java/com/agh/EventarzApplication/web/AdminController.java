package com.agh.EventarzApplication.web;

import com.agh.EventarzApplication.EventarzApplication;
import com.agh.EventarzApplication.feignClients.DataClient;
import com.agh.EventarzApplication.model.EventDTO;
import com.agh.EventarzApplication.model.GroupDTO;
import com.agh.EventarzApplication.model.UserDTO;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    private final static Logger log = LoggerFactory.getLogger(EventarzApplication.class);

    @RequestMapping(value = "/admin/user", method = RequestMethod.GET)
    @Retry(name = "adminGetUserByUuidRetry")
    public String adminGetUserByUuid(@RequestParam String username, Model model, Principal principal) {
        UserDTO user = dataClient.getUser(username);
        if (user == null) {
            log.error("Requested user not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:/home";
        }
        if (user.getSecurityDetailsDTO().getRoles().contains("ADMIN")) {
            model.addAttribute("admin", true);
        }
        model.addAttribute("user", user);
        return "admin/user";
    }

    @RequestMapping(value = "/admin/findGroup", method = RequestMethod.GET)
    @Retry(name = "adminFindGroupRetry")
    public String adminFindGroup(@RequestParam(required = false) String name, Model model) {
        List<GroupDTO> foundGroups = null;
        if (name != null) {
            foundGroups = dataClient.getGroupsByRegex("(?i).*" + name + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundGroups", foundGroups);
        }
        return "admin/findGroup";
    }

    @RequestMapping(value = "/admin/findEvent", method = RequestMethod.GET)
    @Retry(name = "adminFindEventRetry")
    public String adminFindEvent(@RequestParam(required = false) String name, Model model) {
        List<EventDTO> foundEvents = null;
        if (name != null) {
            foundEvents = dataClient.getEventsByRegex("(?i).*" + name + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundEvents", foundEvents);
        }
        return "admin/findEvent";
    }

    @RequestMapping(value = "/admin/findUser", method = RequestMethod.GET)
    @Retry(name = "adminFindUserRetry")
    public String adminFindUser(@RequestParam(required = false) String username, Model model) {
        List<UserDTO> foundUsers = null;
        if (username != null) {
            foundUsers = dataClient.getUsersByRegex("(?i).*" + username + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundUsers", foundUsers);
        }
        return "admin/findUser";
    }

    @RequestMapping(value = "/admin/deleteUser", method = RequestMethod.POST)
    @Retry(name = "adminDeleteUserRetry")
    public String adminDeleteUser(@RequestParam String username, Model model) {
        UserDTO user = dataClient.getUser(username);
        dataClient.deleteUser(username);
        model.addAttribute("infoUserDeleted", true);
        return "redirect:/home";
    }

    @RequestMapping(value = "/admin/deleteEvent", method = RequestMethod.POST)
    @Retry(name = "adminDeleteEventRetry")
    public String adminDeleteEvent(@RequestParam String uuid, Model model) {
        dataClient.deleteEvent(uuid);
        model.addAttribute("infoEventDeleted", true);
        return "redirect:/home";
    }

    @RequestMapping(value = "/admin/deleteGroup", method = RequestMethod.POST)
    @Retry(name = "adminDeleteGroupRetry")
    public String adminDeleteGroup(@RequestParam String uuid, Model model) {
        dataClient.adminDeleteGroup(uuid);
        model.addAttribute("infoGroupDeleted", true);
        return "redirect:/home";
    }
}
