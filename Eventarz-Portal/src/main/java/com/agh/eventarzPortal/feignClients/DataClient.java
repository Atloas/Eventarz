package com.agh.eventarzPortal.feignClients;

import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.EventForm;
import com.agh.eventarzPortal.model.Group;
import com.agh.eventarzPortal.model.GroupForm;
import com.agh.eventarzPortal.model.SecurityDetails;
import com.agh.eventarzPortal.model.User;
import com.agh.eventarzPortal.model.UserForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("eventarz-data-service")
public interface DataClient {

    //Users

    @GetMapping("/users")
    User getUser(@RequestParam String username);

    @GetMapping("/users/uuid")
    String getUuidByUsername(@RequestParam String username);

    @GetMapping("/users/security")
    SecurityDetails getSecurityDetails(@RequestParam String username);

    @GetMapping("users/regex")
    List<User> getUsersByRegex(@RequestParam String regex);

    @PostMapping("/users")
    User createUser(@RequestBody UserForm userForm);

    @DeleteMapping("/users")
    Long deleteUser(@RequestParam String username);

    //Events

    @GetMapping("/events")
    Event getEvent(@RequestParam String uuid);

    @GetMapping("/events/my")
    List<Event> getMyEvents(@RequestParam String username);

    @GetMapping("/events/regex")
    List<Event> getEventsByRegex(@RequestParam String regex);

    @GetMapping("events/allowedToJoin")
    boolean checkIfAllowedToJoinEvent(@RequestParam String uuid, @RequestParam String username);

    @GetMapping("events/allowedToPublish")
    boolean checkIfAllowedToPublishEvent(@RequestParam String groupUuid, @RequestParam String username);

    @PostMapping("/events")
    Event createEvent(@RequestBody EventForm eventForm);

    @PutMapping("/events/join")
    Event joinEvent(@RequestParam String uuid, @RequestParam String username);

    @PutMapping("/events/leave")
    Event leaveEvent(@RequestParam String uuid, @RequestParam String username);

    @DeleteMapping("/events")
    Long deleteEvent(@RequestParam String uuid);

    //Groups

    @GetMapping("/groups")
    Group getGroup(@RequestParam String uuid);

    @GetMapping("/groups/my")
    List<Group> getMyGroups(@RequestParam String username);

    @GetMapping("/groups/regex")
    List<Group> getGroupsByRegex(@RequestParam String regex);

    @PostMapping("/groups")
    Group createGroup(@RequestBody GroupForm groupForm);

    @PutMapping("/groups/join")
    Group joinGroup(@RequestParam String uuid, @RequestParam String username);

    @PutMapping("/groups/leave")
    Group leaveGroup(@RequestParam String uuid, @RequestParam String username);

    @DeleteMapping("/groups")
    Long deleteGroup(@RequestParam String uuid, @RequestParam String username);

    @DeleteMapping("/admin/groups")
    Long adminDeleteGroup(@RequestParam String uuid);
}
