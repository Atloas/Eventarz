package com.agh.eventarzPortal.feignClients;

import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.Group;
import com.agh.eventarzPortal.model.NewEventDTO;
import com.agh.eventarzPortal.model.NewGroupDTO;
import com.agh.eventarzPortal.model.User;
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
    @GetMapping("/events")
    Event getEvent(@RequestParam String uuid);

    @GetMapping("/events/my")
    List<Event> getMyEvents(@RequestParam String username);

    @GetMapping("/events/regex")
    List<Event> getEventsByRegex(@RequestParam String regex);

    @GetMapping("events/allowedToJoin")
    boolean checkIfAllowedToJoinEvent(@RequestParam String uuid, @RequestParam String username);

    @PostMapping("/events")
    Event createEvent(@RequestBody NewEventDTO newEventDTO);

    @PutMapping("/events/join")
    Event joinEvent(@RequestParam String uuid, @RequestBody User user);

    @PutMapping("/events/leave")
    Event leaveEvent(@RequestParam String uuid, @RequestBody User user);

    @DeleteMapping("/events")
    void deleteEvent(@RequestParam String uuid);

    @GetMapping("/groups")
    Group getGroup(@RequestParam String uuid);

    @GetMapping("/groups/my")
    List<Group> getMyGroups(@RequestParam String username);

    @GetMapping("/groups/regex")
    List<Group> getGroupsByRegex(@RequestParam String regex);

    @PostMapping("/groups")
    Group createGroup(@RequestBody NewGroupDTO newEventDTO);

    @PutMapping("/groups/join")
    Group joinGroup(@RequestParam String uuid, @RequestBody User user);

    @PutMapping("/groups/leave")
    Group leaveGroup(@RequestParam String uuid, @RequestBody User user);

    @DeleteMapping("/groups")
    void deleteGroup(@RequestParam String uuid, @RequestParam String username);
}
