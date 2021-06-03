package com.agh.EventarzDataService.services;

import com.agh.EventarzDataService.exceptions.FounderAttemptingToLeaveException;
import com.agh.EventarzDataService.exceptions.GroupNotFoundException;
import com.agh.EventarzDataService.exceptions.UserNotFoundException;
import com.agh.EventarzDataService.model.Group;
import com.agh.EventarzDataService.model.GroupDTO;
import com.agh.EventarzDataService.model.GroupForm;
import com.agh.EventarzDataService.model.User;
import com.agh.EventarzDataService.repositories.EventRepository;
import com.agh.EventarzDataService.repositories.GroupRepository;
import com.agh.EventarzDataService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    public GroupDTO getGroupByUuid(String uuid) throws GroupNotFoundException {
        Group group = groupRepository.findByUuid(uuid);
        if (group == null) {
            throw new GroupNotFoundException("Group " + uuid + " not found!");
        }
        GroupDTO groupDTO = group.createDTO();
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

    public GroupDTO createGroup(GroupForm groupForm) throws UserNotFoundException {
        User founder = userRepository.findByUsername(groupForm.getFounderUsername());
        if (founder == null) {
            throw new UserNotFoundException("Founder " + groupForm.getFounderUsername() + " not found!");
        }
        Group group = new Group(groupForm, founder);
        group = groupRepository.save(group);
        //TODO: Is this necessary?
        groupRepository.join(group.getUuid(), founder.getUsername());
        GroupDTO groupDTO = group.createDTO();
        return groupDTO;
    }

    public GroupDTO updateGroup(String uuid, GroupForm groupForm) throws GroupNotFoundException {
        Group group = groupRepository.findByUuid(uuid);
        if (group == null) {
            throw new GroupNotFoundException("Group " + uuid + " not found!");
        }
        group.setName(groupForm.getName());
        group.setDescription(groupForm.getDescription());
        group = groupRepository.save(group);
        GroupDTO groupDTO = group.createDTO();
        return groupDTO;
    }

    public GroupDTO joinGroup(String uuid, String username) throws UserNotFoundException, GroupNotFoundException {
        //TODO: Parallelize this and others like this?
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User " + username + " not found!");
        }
        Group group = groupRepository.findByUuid(uuid);
        if (group == null) {
            throw new GroupNotFoundException("Group " + uuid + " not found!");
        }
        group.joinedBy(user);
        //TODO: Is this necessary? Replace with save?
        groupRepository.join(uuid, username);
        GroupDTO groupDTO = group.createDTO();
        return groupDTO;
    }

    public GroupDTO leaveGroup(String uuid, String username) throws UserNotFoundException, GroupNotFoundException, FounderAttemptingToLeaveException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User " + username + " not found!");
        }
        Group group = groupRepository.findByUuid(uuid);
        if (group == null) {
            throw new GroupNotFoundException("Group " + uuid + " not found!");
        }
        if (group.getFounder().getUsername().equals(user.getUsername())) {
            throw new FounderAttemptingToLeaveException("Founders are not allowed to leave their groups, they can only delete them!");
        }
        group.leftBy(user.getUsername());
        eventRepository.deleteFromGroupByOrganizerUsername(group.getUuid(), username);
        groupRepository.leave(user.getUsername(), uuid);
        group = groupRepository.findByUuid(uuid);
        // TODO: Disband group if founder leaves?
        GroupDTO groupDTO = group.createDTO();
        return groupDTO;
    }

    public String deleteGroup(String uuid) {
        groupRepository.deleteByUuid(uuid);
        return uuid;
    }
}
