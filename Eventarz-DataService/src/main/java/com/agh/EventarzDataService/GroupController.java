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
import java.util.Optional;

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
        Optional<Group> group = groupRepository.findByUuid(uuid);
        GroupDTO groupDTO = null;
        if (group.isPresent()) {
            groupDTO = group.get().createDTO();
        }
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
        Optional<User> founder = userRepository.findByUsername(groupForm.getFounderUsername());
        if (founder.isPresent()) {
            Group newGroup = Group.of(groupForm.getName(), groupForm.getDescription(), new ArrayList<>(), new ArrayList<>(), founder.get());
            newGroup = groupRepository.save(newGroup);
            //TODO: Is this necessary?
            groupRepository.belongsTo(newGroup.getUuid(), founder.get().getUsername());
            GroupDTO newGroupDTO = newGroup.createDTO();
            return newGroupDTO;
        } else {
            //TODO: Some error handling or reporting?
            return null;
        }
    }

    @PutMapping(value = "/groups/join")
    @Transactional
    @Retry(name = "joinGroupRetry")
    public GroupDTO joinGroup(@RequestParam String uuid, @RequestParam String username) {
        //TODO: Parallelize this and others like this?
        Optional<User> user = userRepository.findByUsername(username);
        Optional<Group> group = groupRepository.findByUuid(uuid);
        GroupDTO groupDTO = null;
        if (user.isPresent() && group.isPresent()) {
            group.get().joinedBy(user.get());
            //TODO: Is this necessary?
            groupRepository.belongsTo(uuid, username);
            groupDTO = group.get().createDTO();
        }
        return groupDTO;
    }

    @PutMapping(value = "/groups/leave")
    @Transactional
    @Retry(name = "leaveGroupRetry")
    public GroupDTO leaveGroup(@RequestParam String uuid, @RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Optional<Group> group = groupRepository.findByUuid(uuid);
        GroupDTO groupDTO = null;
        if (user.isPresent() && group.isPresent()) {
            //TODO: A variable in stead of constant gets
            group.get().leftBy(user.get().getUsername());
            //TODO: Is this necessary?
            groupRepository.leftBy(user.get().getUsername(), uuid);
            if (group.get().getMembers().size() == 0) {
                groupRepository.deleteByUuid(group.get().getUuid());
                //TODO: Should this be like this?
                return null;
            }
            groupDTO = group.get().createDTO();
        }
        return groupDTO;
    }

    @DeleteMapping(value = "/groups")
    @Transactional
    @Retry(name = "deleteGroupRetry")
    public Long deleteGroup(@RequestParam String uuid, @RequestParam String username) {
        return groupRepository.deleteByUuid(uuid).get();
    }

    @DeleteMapping(value = "/admin/groups")
    @Transactional
    @Retry(name = "adminDeleteGroupRetry")
    public Long adminDeleteGroup(@RequestParam String uuid) {
        return groupRepository.deleteByUuid(uuid).get();
    }
}
