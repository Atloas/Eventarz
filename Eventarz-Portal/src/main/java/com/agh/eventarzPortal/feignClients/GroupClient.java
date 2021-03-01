package com.agh.eventarzPortal.feignClients;

import com.agh.eventarzPortal.model.Group;
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

@FeignClient("eventarz-group-service")
public interface GroupClient {
    @GetMapping("/groups")
    Group get(@RequestParam String uuid);

    @GetMapping("/groups/my")
    List<Group> getMy(@RequestParam String username);

    @GetMapping("/groups/regex")
    List<Group> getRegex(@RequestParam String regex);

    @PostMapping("/groups")
    Group create(@RequestBody NewGroupDTO newEventDTO);

    @PutMapping("/groups/join")
    Group join(@RequestParam String uuid, @RequestBody User user);

    @PutMapping("/groups/leave")
    Group leave(@RequestParam String uuid, @RequestBody User user);

    @DeleteMapping("/groups")
    void delete(@RequestParam String uuid);
}
