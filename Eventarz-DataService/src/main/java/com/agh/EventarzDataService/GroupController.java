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
package com.agh.EventarzDataService;

import com.agh.EventarzDataService.model.Group;
import com.agh.EventarzDataService.model.GroupDTO;
import com.agh.EventarzDataService.model.GroupForm;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.repositories.EventRepository;
import com.agh.EventarzDataService.repositories.GroupRepository;
import com.agh.EventarzDataService.repositories.UserRepository;
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

import java.util.ArrayList;
import java.util.List;

@RestController
public class GroupController {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    private final static Logger log = LoggerFactory.getLogger(EventarzDataServiceApplication.class);

    @GetMapping(value = "/groups")
    @Transactional
    @Retry(name = "getGroupByUuidRetry")
    public GroupDTO getGroupByUuid(@RequestParam String uuid) {
        Group group = groupRepository.findByUuid(uuid);
        GroupDTO groupDTO = group.createDTO();
        //TODO: group not found
        return groupDTO;
    }

    @GetMapping(value = "/groups/my")
    @Transactional
    @Retry(name = "getMyGroupsRetry")
    public List<GroupDTO> getMyGroups(@RequestParam String username) {
        List<Group> groups = groupRepository.findMyGroups(username);
        List<GroupDTO> groupDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupDTOs.add(group.createDTO());
        }
        return groupDTOs;
    }

    @GetMapping(value = "/groups/regex")
    @Transactional
    @Retry(name = "getGroupsRegexRetry")
    public List<GroupDTO> getGroupsRegex(@RequestParam String regex) {
        List<Group> groups = groupRepository.findByNameRegex(regex);
        List<GroupDTO> groupDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupDTOs.add(group.createDTO());
        }
        return groupDTOs;
    }

    @PostMapping(value = "/groups")
    @Transactional
    @Retry(name = "createGroupRetry")
    public GroupDTO createGroup(@RequestBody GroupForm groupForm) {
        //Assumes valid groupForm
        User founder = userRepository.findByUsername(groupForm.getFounderUsername());
        Group newGroup = Group.of(groupForm.getName(), groupForm.getDescription(), new ArrayList<>(), new ArrayList<>(), founder);
        newGroup = groupRepository.save(newGroup);
        //TODO: Is this necessary?
        groupRepository.belongsTo(newGroup.getUuid(), founder.getUsername());
        GroupDTO newGroupDTO = newGroup.createDTO();
        return newGroupDTO;
    }

    @PutMapping(value = "/groups/join")
    @Transactional
    @Retry(name = "joinGroupRetry")
    public GroupDTO joinGroup(@RequestParam String uuid, @RequestParam String username) {
        User user = userRepository.findByUsername(username);
        Group group = groupRepository.findByUuid(uuid);
        group.joinedBy(user);
        //TODO: Is this necessary?
        groupRepository.belongsTo(uuid, username);
        GroupDTO groupDTO = group.createDTO();
        return groupDTO;
    }

    @PutMapping(value = "/groups/leave")
    @Transactional
    @Retry(name = "leaveGroupRetry")
    public GroupDTO leaveGroup(@RequestParam String uuid, @RequestParam String username) {
        User user = userRepository.findByUsername(username);
        Group group = groupRepository.findByUuid(uuid);
        group.leftBy(user.getUsername());
        //TODO: Is this necessary?
        groupRepository.leftBy(user.getUsername(), uuid);
        if (group.getMembers().size() == 0) {
            groupRepository.deleteByUuid(group.getUuid());
            //TODO: Should this be like this?
            return null;
        }
        GroupDTO groupDTO = group.createDTO();
        return groupDTO;
    }

    @DeleteMapping(value = "/groups")
    @Transactional
    @Retry(name = "deleteGroupRetry")
    public Long deleteGroup(@RequestParam String uuid, @RequestParam String username) {
        if (groupRepository.isFounder(uuid, username)) {
            return groupRepository.deleteByUuid(uuid);
        }
        //TODO: Error handling
        return (long) -1;
    }

    @DeleteMapping(value = "/admin/groups")
    @Transactional
    @Retry(name = "adminDeleteGroupRetry")
    public Long adminDeleteGroup(@RequestParam String uuid) {
        return groupRepository.deleteByUuid(uuid);
    }
}
