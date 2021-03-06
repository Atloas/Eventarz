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
import com.agh.EventarzDataService.model.GroupForm;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.repositories.EventRepository;
import com.agh.EventarzDataService.repositories.GroupRepository;
import com.agh.EventarzDataService.repositories.UserRepository;
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

    @Transactional
    @GetMapping(value = "/groups")
    public Group getGroupByUuid(@RequestParam String uuid) {
        Group group = groupRepository.findByUuid(uuid);
        //TODO: group not found
        return group;
    }

    @Transactional
    @GetMapping(value = "/groups/my")
    public List<Group> getMyGroups(@RequestParam String username) {
        List<Group> groups = groupRepository.findMyGroups(username);
        return groups;
    }

    @Transactional
    @GetMapping(value = "/groups/regex")
    public List<Group> getGroupsRegex(@RequestParam String regex) {
        List<Group> groups = groupRepository.findByNameRegex(regex);
        return groups;
    }

    @Transactional
    @PostMapping(value = "/groups")
    public Group createGroup(@RequestBody GroupForm groupForm) {
        //Assumes valid groupForm
        User founder = userRepository.findByUsername(groupForm.getFounderUsername());
        Group newGroup = Group.of(groupForm.getName(), groupForm.getDescription(), false, new ArrayList<>(), new ArrayList<>(), founder);
        newGroup = groupRepository.save(newGroup);
        //TODO: Is this necessary?
        groupRepository.belongsTo(newGroup.getUuid(), founder.getUsername());
        return newGroup;
    }

    @Transactional
    @PutMapping(value = "/groups/join")
    public Group joinGroup(@RequestParam String uuid, @RequestParam String username) {
        User user = userRepository.findByUsername(username);
        Group group = groupRepository.findByUuid(uuid);
        group.joinedBy(user);
        //TODO: Is this necessary?
        groupRepository.belongsTo(uuid, username);
        return group;
    }

    @Transactional
    @PutMapping(value = "/groups/leave")
    public Group leaveGroup(@RequestParam String uuid, @RequestParam String username) {
        User user = userRepository.findByUsername(username);
        Group group = groupRepository.findByUuid(uuid);
        group.leftBy(user.getUsername());
        //TODO: Is this necessary?
        groupRepository.leftBy(user.getUsername(), uuid);
        if (group.getMembers().size() == 0) {
            groupRepository.deleteByUuid(group.getUuid());
        }
        return group;
    }

    @Transactional
    @DeleteMapping(value = "/groups")
    public Long deleteGroup(@RequestParam String uuid, @RequestParam String username) {
        if (groupRepository.isFounder(uuid, username)) {
            return groupRepository.deleteByUuid(uuid);
        }
        //TODO: Error handling
        return (long) -1;
    }

    @Transactional
    @DeleteMapping(value = "/admin/groups")
    public Long adminDeleteGroup(@RequestParam String uuid) {
        return groupRepository.deleteByUuid(uuid);
    }
}
