package com.agh.eventarzPortal.web;

import com.agh.eventarzPortal.EventarzPortalApplication;
import com.agh.eventarzPortal.feignClients.EventClient;
import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.Group;
import com.agh.eventarzPortal.model.User;
import com.agh.eventarzPortal.repositories.GroupRepository;
import com.agh.eventarzPortal.repositories.UserRepository;
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
import java.util.Set;

/**
 * This Controller handles requests regarding the administrative tasks of this application. They all require the ADMIN authority.
 */
@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private EventClient eventClient;

    private final static Logger log = LoggerFactory.getLogger(EventarzPortalApplication.class);

    /**
     * Returns a view containing the details of the requested User.
     *
     * @param uuid      Uuid of the desired User.
     * @param model     MVC model.
     * @param principal Logged in User.
     * @return /admin/user view.
     */
    @Transactional
    @RequestMapping(value = "/admin/user", method = RequestMethod.GET)
    public String adminGetUserByUuid(@RequestParam String uuid, Model model, Principal principal) {
        User user = userRepository.findByUuid(uuid);
        if (user == null) {
            log.error("Requested user not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:/home";
        }
        if (user.getRoles().contains("ADMIN")) {
            model.addAttribute("admin", true);
        }
        model.addAttribute("user", user);
        return "admin/user";
    }

    /**
     * Returns a view allowing to search the database for Groups, or containing the search results if name is provided.
     *
     * @param name  Optional, name to search for.
     * @param model MVC model.
     * @return /admin/findGroup view.
     */
    @Transactional
    @RequestMapping(value = "/admin/findGroup", method = RequestMethod.GET)
    public String adminFindGroup(@RequestParam(required = false) String name, Model model) {
        Set<Group> foundGroups = null;
        if (name != null) {
            foundGroups = groupRepository.findByNameRegex("(?i).*" + name + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundGroups", foundGroups);
        }
        return "admin/findGroup";
    }

    /**
     * Returns a view allowing to search the database for Events, or containing the search results if name is provided.
     *
     * @param name  Optional, name to search for.
     * @param model MVC model.
     * @return /admin/findEvent view.
     */
    @Transactional
    @RequestMapping(value = "/admin/findEvent", method = RequestMethod.GET)
    public String adminFindEvent(@RequestParam(required = false) String name, Model model) {
        List<Event> foundEvents = null;
        if (name != null) {
            foundEvents = eventClient.getRegex("(?i).*" + name + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundEvents", foundEvents);
        }
        return "admin/findEvent";
    }

    /**
     * Returns a view allowing to search the database for Users, or containing the search results if username is provided.
     *
     * @param username Optional, username to search for.
     * @param model    MVC model.
     * @return /admin/findUser view.
     */
    @Transactional
    @RequestMapping(value = "/admin/findUser", method = RequestMethod.GET)
    public String adminFindUser(@RequestParam(required = false) String username, Model model) {
        Set<User> foundUsers = null;
        if (username != null) {
            foundUsers = userRepository.findByUsernameRegex("(?i).*" + username + ".*");
            model.addAttribute("searched", true);
            model.addAttribute("foundUsers", foundUsers);
        }
        return "admin/findUser";
    }

    /**
     * Deletes the specified User.
     *
     * @param uuid  Identifier of the User to delete.
     * @param model MVC model.
     * @return A redirect back to the home page.
     */
    @Transactional
    @RequestMapping(value = "/admin/deleteUser", method = RequestMethod.POST)
    public String adminDeleteUser(@RequestParam String uuid, Model model) {
        User user = userRepository.findByUuid(uuid);
        if (user == null) {
            log.error("Requested event not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:/home";
        }
        if (user.getRoles().contains("ADMIN")) {
            log.error("Attempt to delete admin user!");
            return "redirect:/home";
        }
        userRepository.delete(user);
        model.addAttribute("infoUserDeleted", true);
        return "redirect:/home";
    }

    /**
     * Deletes the specified Event.
     *
     * @param uuid  Identifier of the Event to delete.
     * @param model MVC model.
     * @return A redirect back to the home page.
     */
    @Transactional
    @RequestMapping(value = "/admin/deleteEvent", method = RequestMethod.POST)
    public String adminDeleteEvent(@RequestParam String uuid, Model model) {
        eventClient.delete(uuid);
        model.addAttribute("infoEventDeleted", true);
        return "redirect:/home";
    }

    /**
     * Deletes the specified Group.
     *
     * @param uuid  Identifier of the Group to delete.
     * @param model MVC model.
     * @return A redirect back to the home page.
     */
    @Transactional
    @RequestMapping(value = "/admin/deleteGroup", method = RequestMethod.POST)
    public String adminDeleteGroup(@RequestParam String uuid, Model model) {
        Group group = groupRepository.findByUuid(uuid);
        if (group == null) {
            log.error("Requested group not returned from DB!");
            model.addAttribute("errorDb", true);
            return "redirect:/home";
        }
        groupRepository.delete(group);
        model.addAttribute("infoGroupDeleted", true);
        return "redirect:/home";
    }
}
