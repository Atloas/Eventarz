package com.agh.EventarzGateway.feignClients;

import com.agh.EventarzGateway.model.BanForm;
import com.agh.EventarzGateway.model.Event;
import com.agh.EventarzGateway.model.EventForm;
import com.agh.EventarzGateway.model.Group;
import com.agh.EventarzGateway.model.GroupForm;
import com.agh.EventarzGateway.model.SecurityDetails;
import com.agh.EventarzGateway.model.User;
import com.agh.EventarzGateway.model.UserForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("eventarz-data-service")
public interface DataClient {

    //Users

    @GetMapping("/users")
    List<User> getUsersByRegex(@RequestParam String regex);

    @PostMapping("/users")
    User createUser(@RequestBody UserForm userForm);

    @GetMapping("/users/{username}")
    User getUser(@PathVariable String username);

    @DeleteMapping("/users/{username}")
    String deleteUser(@PathVariable String username);

    @RequestMapping(method = RequestMethod.HEAD, value = "/users/{username}")
    void checkIfUserExists(@PathVariable String username);

    @GetMapping("/users/{username}/securityDetails")
    SecurityDetails getSecurityDetails(@PathVariable String username);

    @PutMapping("users/{username}/securityDetails/banned")
    User changeBanStatus(@PathVariable String username, @RequestBody BanForm banForm);

    //Events

    @GetMapping("/events")
    List<Event> getMyEvents(@RequestParam String username);

    @GetMapping("/events")
    List<Event> getEventsByRegex(@RequestParam String regex);

    @PostMapping("/events")
    Event createEvent(@RequestBody EventForm eventForm);

    @DeleteMapping("/events/{uuid}")
    String deleteEvent(@PathVariable String uuid);

    @GetMapping("/events/{uuid}")
    Event getEvent(@PathVariable String uuid);

    @PutMapping("/events/{uuid}")
    Event updateEvent(@PathVariable String uuid, @RequestBody EventForm eventForm);

    @PostMapping("/events/{uuid}/participants")
    Event joinEvent(@PathVariable String uuid, @RequestBody String username);

    @DeleteMapping("/events/{uuid}/participants/{username}")
    Event leaveEvent(@PathVariable String uuid, @PathVariable String username);


    //Groups

    @GetMapping("/groups")
    List<Group> getMyGroups(@RequestParam String username);

    @GetMapping("/groups")
    List<Group> getGroupsByRegex(@RequestParam String regex);

    @PostMapping("/groups")
    Group createGroup(@RequestBody GroupForm groupForm);

    @GetMapping("/groups/{uuid}")
    Group getGroup(@PathVariable String uuid);

    @PutMapping("/groups/{uuid}")
    Group updateGroup(@PathVariable String uuid, @RequestBody GroupForm groupForm);

    @DeleteMapping("/groups/{uuid}")
    String deleteGroup(@PathVariable String uuid);

    @PostMapping("/groups/{uuid}/members")
    Group joinGroup(@PathVariable String uuid, @RequestBody String username);

    @DeleteMapping("/groups/{uuid}/members/{username}")
    Group leaveGroup(@PathVariable String uuid, @PathVariable String username);
}
