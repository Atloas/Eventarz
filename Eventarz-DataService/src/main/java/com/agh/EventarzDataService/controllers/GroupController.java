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
import com.agh.EventarzDataService.model.GroupDTO;
import com.agh.EventarzDataService.model.GroupForm;
import com.agh.EventarzDataService.services.GroupService;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GroupController {

    @Autowired
    private GroupService groupService;

    private final static Logger log = LoggerFactory.getLogger(EventarzDataServiceApplication.class);

    @GetMapping(value = "/groups")
    @Transactional
    @Retry(name = "getGroupByUuidRetry")
    public GroupDTO getGroupByUuid(@RequestParam String uuid) {
        GroupDTO groupDTO = groupService.getGroupByUuid(uuid);
        return groupDTO;
    }

    @GetMapping(value = "/groups/my")
    @Transactional
    @Retry(name = "getMyGroupsRetry")
    public List<GroupDTO> getMyGroups(@RequestParam String username) {
        List<GroupDTO> groupDTOs = groupService.getMyGroups(username);
        return groupDTOs;
    }

    @GetMapping(value = "/groups/regex")
    @Transactional
    @Retry(name = "getGroupsRegexRetry")
    public List<GroupDTO> getGroupsRegex(@RequestParam String regex) {
        List<GroupDTO> groupDTOs = groupService.getGroupsRegex(regex);
        return groupDTOs;
    }

    @PostMapping(value = "/groups")
    @Transactional
    @Retry(name = "createGroupRetry")
    public GroupDTO createGroup(@RequestBody GroupForm groupForm) {
        GroupDTO groupDTO = groupService.createGroup(groupForm);
        return groupDTO;
    }

    @PutMapping(value = "/groups/join")
    @Transactional
    @Retry(name = "joinGroupRetry")
    public GroupDTO joinGroup(@RequestParam String uuid, @RequestParam String username) {
        GroupDTO groupDTO = groupService.joinGroup(uuid, username);
        return groupDTO;
    }

    @PutMapping(value = "/groups/leave")
    @Transactional
    @Retry(name = "leaveGroupRetry")
    public GroupDTO leaveGroup(@RequestParam String uuid, @RequestParam String username) {
        GroupDTO groupDTO = groupService.leaveGroup(uuid, username);
        return groupDTO;
    }

    @DeleteMapping(value = "/groups")
    @Transactional
    @Retry(name = "deleteGroupRetry")
    public Long deleteGroup(@RequestParam String uuid, @RequestParam String username) {
        Long id = groupService.deleteGroup(uuid, username);
        return id;
    }

    @DeleteMapping(value = "/admin/groups")
    @Transactional
    @Retry(name = "adminDeleteGroupRetry")
    public Long adminDeleteGroup(@RequestParam String uuid) {
        Long id = groupService.adminDeleteGroup(uuid);
        return id;
    }
}
