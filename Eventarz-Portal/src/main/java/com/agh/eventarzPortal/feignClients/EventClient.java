package com.agh.eventarzPortal.feignClients;

import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.NewEventDTO;
import com.agh.eventarzPortal.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("eventarz-event-service")
public interface EventClient {
    @GetMapping("/events")
    Event get(@RequestParam String uuid);

    @GetMapping("/events/my")
    List<Event> getMy(@RequestParam String username);

    @GetMapping("/events/regex")
    List<Event> getRegex(@RequestParam String regex);

    @GetMapping("events/allowedToJoin")
    boolean checkIfAllowedToJoin(@RequestParam String uuid, @RequestParam String username);

    @PostMapping("/events")
    Event create(@RequestBody NewEventDTO newEventDTO);

    @PutMapping("/events/join")
    Event join(@RequestParam String uuid, @RequestBody User user);

    @PutMapping("/events/leave")
    Event leave(@RequestParam String uuid, @RequestBody User user);

    @DeleteMapping("/events")
    void delete(@RequestParam String uuid);
}
