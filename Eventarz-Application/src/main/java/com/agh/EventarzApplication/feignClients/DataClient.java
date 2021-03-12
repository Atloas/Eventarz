package com.agh.EventarzApplication.feignClients;

import com.agh.EventarzApplication.model.EventDTO;
import com.agh.EventarzApplication.model.EventForm;
import com.agh.EventarzApplication.model.GroupDTO;
import com.agh.EventarzApplication.model.GroupForm;
import com.agh.EventarzApplication.model.SecurityDetailsDTO;
import com.agh.EventarzApplication.model.UserDTO;
import com.agh.EventarzApplication.model.UserForm;
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
    UserDTO getUser(@RequestParam String username);

    @GetMapping("/users/uuid")
    String getUuidByUsername(@RequestParam String username);

    @GetMapping("/users/security")
    SecurityDetailsDTO getSecurityDetails(@RequestParam String username);

    @GetMapping("users/regex")
    List<UserDTO> getUsersByRegex(@RequestParam String regex);

    @PostMapping("/users")
    UserDTO createUser(@RequestBody UserForm userForm);

    @DeleteMapping("/users")
    Long deleteUser(@RequestParam String username);

    //Events

    @GetMapping("/events")
    EventDTO getEvent(@RequestParam String uuid);

    @GetMapping("/events/my")
    List<EventDTO> getMyEvents(@RequestParam String username);

    @GetMapping("/events/regex")
    List<EventDTO> getEventsByRegex(@RequestParam String regex);

    @GetMapping("events/allowedToJoin")
    boolean checkIfAllowedToJoinEvent(@RequestParam String uuid, @RequestParam String username);

    @GetMapping("events/allowedToPublish")
    boolean checkIfAllowedToPublishEvent(@RequestParam String groupUuid, @RequestParam String username);

    @PostMapping("/events")
    EventDTO createEvent(@RequestBody EventForm eventForm);

    @PutMapping("/events/join")
    EventDTO joinEvent(@RequestParam String uuid, @RequestParam String username);

    @PutMapping("/events/leave")
    EventDTO leaveEvent(@RequestParam String uuid, @RequestParam String username);

    @DeleteMapping("/events")
    Long deleteEvent(@RequestParam String uuid);

    //Groups

    @GetMapping("/groups")
    GroupDTO getGroup(@RequestParam String uuid);

    @GetMapping("/groups/my")
    List<GroupDTO> getMyGroups(@RequestParam String username);

    @GetMapping("/groups/regex")
    List<GroupDTO> getGroupsByRegex(@RequestParam String regex);

    @PostMapping("/groups")
    GroupDTO createGroup(@RequestBody GroupForm groupForm);

    @PutMapping("/groups/join")
    GroupDTO joinGroup(@RequestParam String uuid, @RequestParam String username);

    @PutMapping("/groups/leave")
    GroupDTO leaveGroup(@RequestParam String uuid, @RequestParam String username);

    @DeleteMapping("/groups")
    Long deleteGroup(@RequestParam String uuid, @RequestParam String username);

    @DeleteMapping("/admin/groups")
    Long adminDeleteGroup(@RequestParam String uuid);
}
