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
package com.agh.EventarzGroupService;

import com.agh.EventarzGroupService.model.Group;
import com.agh.EventarzGroupService.model.GroupForm;
import com.agh.EventarzGroupService.model.NewGroupDTO;
import com.agh.EventarzGroupService.model.User;
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
    private GroupRepository groupRepository;

    private final static Logger log = LoggerFactory.getLogger(EventarzGroupServiceApplication.class);

    @Transactional
    @GetMapping(value = "/groups")
    public Group getGroupByUuid(@RequestParam String uuid) {
        Group event = groupRepository.findByUuid(uuid);
        //TODO: This still modifies event and serializableGroup
        Group serializableGroup = event.createSerializableCopy();
        //TODO: group not found
        return serializableGroup;
    }

    @Transactional
    @GetMapping(value = "/groups/my")
    public List<Group> getMyGroups(@RequestParam String username) {
        List<Group> groups = groupRepository.findMyGroups(username);
        List<Group> serializableGroups = new ArrayList<>();
        for (Group event : groups) {
            serializableGroups.add(event.createSerializableCopy());
        }
        return serializableGroups;
    }

    @Transactional
    @GetMapping(value = "/groups/regex")
    public List<Group> getGroupsRegex(@RequestParam String regex) {
        List<Group> groups = groupRepository.findByNameRegex(regex);
        List<Group> serializableGroups = new ArrayList<>();
        for (Group group : groups) {
            serializableGroups.add(group.createSerializableCopy());
        }
        return serializableGroups;
    }

    @Transactional
    @PostMapping(value = "/groups")
    public Group createGroup(@RequestBody NewGroupDTO newGroupDTO) {
        //Assumes valid eventForm
        GroupForm groupForm = newGroupDTO.getGroupForm();
        Group newGroup = Group.of(groupForm.getName(), groupForm.getDescription(), new ArrayList<>(), new ArrayList<>(), newGroupDTO.getFounder());
        newGroup = groupRepository.save(newGroup);
        groupRepository.belongsTo(newGroup.getUuid(), newGroupDTO.getFounder().getUsername());
        return newGroup.createSerializableCopy();
    }

    @Transactional
    @PutMapping(value = "/groups/join")
    public Group joinGroup(@RequestParam String uuid, @RequestBody User user) {
        Group group = groupRepository.findByUuid(uuid);
        group.joinedBy(user);
        groupRepository.belongsTo(group.getUuid(), user.getUsername());
        return group.createSerializableCopy();
    }

    @Transactional
    @PutMapping(value = "/groups/leave")
    public Group leaveGroup(@RequestParam String uuid, @RequestBody User user) {
        Group group = groupRepository.findByUuid(uuid);
        group.leftBy(user.getUsername());
        groupRepository.leftBy(user.getUsername(), uuid);
        return group.createSerializableCopy();
    }

    @Transactional
    @DeleteMapping(value = "/groups")
    public Long deleteGroup(@RequestParam String uuid) {
        return groupRepository.deleteByUuid(uuid);
    }
}
