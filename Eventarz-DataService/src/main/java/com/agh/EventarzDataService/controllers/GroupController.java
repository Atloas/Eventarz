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
package com.agh.EventarzDataService.controllers;

import com.agh.EventarzDataService.EventarzDataServiceApplication;
import com.agh.EventarzDataService.exceptions.FounderAttemptingToLeaveException;
import com.agh.EventarzDataService.exceptions.GroupNotFoundException;
import com.agh.EventarzDataService.exceptions.UserNotFoundException;
import com.agh.EventarzDataService.model.GroupDTO;
import com.agh.EventarzDataService.model.GroupForm;
import com.agh.EventarzDataService.services.GroupService;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GroupController {

    @Autowired
    private GroupService groupService;

    private final static Logger log = LoggerFactory.getLogger(EventarzDataServiceApplication.class);

    @GetMapping(value = "/groups", params = {"username"})
    @Transactional
    @Retry(name = "getMyGroupsRetry")
    public List<GroupDTO> getMyGroups(@RequestParam String username) {
        List<GroupDTO> groupDTOs = groupService.getMyGroups(username);
        return groupDTOs;
    }

    @GetMapping(value = "/groups", params = {"regex"})
    @Transactional
    @Retry(name = "getGroupsRegexRetry")
    public List<GroupDTO> getGroupsRegex(@RequestParam String regex) {
        List<GroupDTO> groupDTOs = groupService.getGroupsRegex(regex);
        return groupDTOs;
    }

    @PostMapping("/groups")
    @Transactional
    @Retry(name = "createGroupRetry")
    public GroupDTO createGroup(@RequestBody GroupForm groupForm) {
        try {
            GroupDTO groupDTO = groupService.createGroup(groupForm);
            return groupDTO;
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Founder not found!", e);
        }
    }

    @GetMapping("/groups/{uuid}")
    @Transactional
    @Retry(name = "getGroupByUuidRetry")
    public GroupDTO getGroupByUuid(@PathVariable String uuid) {
        try {
            GroupDTO groupDTO = groupService.getGroupByUuid(uuid);
            return groupDTO;
        } catch (GroupNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found!", e);
        }
    }

    @PutMapping("/groups/{uuid}")
    @Transactional
    @Retry(name = "getGroupByUuidRetry")
    public GroupDTO updateGroup(@PathVariable String uuid, @RequestBody GroupForm groupForm) {
        try {
            GroupDTO groupDTO = groupService.updateGroup(uuid, groupForm);
            return groupDTO;
        } catch (GroupNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found!", e);
        }
    }

    @DeleteMapping("/groups/{uuid}")
    @Transactional
    @Retry(name = "deleteGroupRetry")
    public String deleteGroup(@PathVariable String uuid) {
        groupService.deleteGroup(uuid);
        return uuid;
    }

    @PostMapping("/groups/{uuid}/members")
    @Transactional
    @Retry(name = "joinGroupRetry")
    public GroupDTO joinGroup(@PathVariable String uuid, @RequestBody String username) {
        try {
            GroupDTO groupDTO = groupService.joinGroup(uuid, username);
            return groupDTO;
        } catch (GroupNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found!", e);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!", e);
        }
    }

    @DeleteMapping("/groups/{uuid}/members/{username}")
    @Transactional
    @Retry(name = "leaveGroupRetry")
    public GroupDTO leaveGroup(@PathVariable String uuid, @PathVariable String username) {
        try {
            GroupDTO groupDTO = groupService.leaveGroup(uuid, username);
            return groupDTO;
        } catch (GroupNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found!", e);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!", e);
        } catch (FounderAttemptingToLeaveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The founder cannot leave their Group!", e);
        }
    }


    @DeleteMapping("/admin/groups/{uuid}")
    @Transactional
    @Retry(name = "adminDeleteGroupRetry")
    public ResponseEntity<String> adminDeleteGroup(@PathVariable String uuid) {
        groupService.deleteGroup(uuid);
        return ResponseEntity.ok(uuid);
    }
}
