package com.agh.EventarzDataService.services;

import com.agh.EventarzDataService.model.Group;
import com.agh.EventarzDataService.model.GroupDTO;
import com.agh.EventarzDataService.model.GroupForm;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.repositories.GroupRepository;
import com.agh.EventarzDataService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    public GroupDTO getGroupByUuid(String uuid) {
        Optional<Group> group = groupRepository.findByUuid(uuid);
        GroupDTO groupDTO = null;
        if (group.isPresent()) {
            groupDTO = group.get().createDTO();
        }
        return groupDTO;
    }

    public List<GroupDTO> getMyGroups(String username) {
        List<Group> groups = groupRepository.findMyGroups(username);
        List<GroupDTO> groupDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupDTOs.add(group.createDTO());
        }
        return groupDTOs;
    }

    public List<GroupDTO> getGroupsRegex(String regex) {
        List<Group> groups = groupRepository.findByNameRegex(regex);
        List<GroupDTO> groupDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupDTOs.add(group.createDTO());
        }
        return groupDTOs;
    }

    public GroupDTO createGroup(GroupForm groupForm) {
        Optional<User> founder = userRepository.findByUsername(groupForm.getFounderUsername());
        if (founder.isPresent()) {
            Group newGroup = Group.of(groupForm.getName(), groupForm.getDescription(), new ArrayList<>(), new ArrayList<>(), founder.get());
            newGroup = groupRepository.save(newGroup);
            //TODO: Is this necessary?
            groupRepository.belongsTo(newGroup.getUuid(), founder.get().getUsername());
            return newGroup.createDTO();
        } else {
            //TODO: Some error handling or reporting?
            return null;
        }
    }

    public GroupDTO joinGroup(String uuid, String username) {
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

    public GroupDTO leaveGroup(String uuid, String username) {
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

    public Long deleteGroup(String uuid, String username) {
        return groupRepository.deleteByUuid(uuid).orElse(null);
    }

    public Long adminDeleteGroup(String uuid) {
        return groupRepository.deleteByUuid(uuid).orElse(null);
    }
}
