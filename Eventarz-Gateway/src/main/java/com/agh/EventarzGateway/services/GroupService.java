package com.agh.EventarzGateway.services;

import com.agh.EventarzGateway.exceptions.NotFounderException;
import com.agh.EventarzGateway.feignClients.DataClient;
import com.agh.EventarzGateway.model.Group;
import com.agh.EventarzGateway.model.GroupForm;
import com.agh.EventarzGateway.model.dtos.GroupDTO;
import com.agh.EventarzGateway.model.dtos.GroupSearchedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private DataClient dataClient;

    public List<GroupSearchedDTO> getMyGroups(Principal principal) {
        List<Group> groups = dataClient.getMyGroups(principal.getName());
        List<GroupSearchedDTO> groupSearchedDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupSearchedDTOs.add(new GroupSearchedDTO(group));
        }
        return groupSearchedDTOs;
    }

    public List<GroupSearchedDTO> getGroupsByName(String name) {
        List<Group> groups = dataClient.getGroupsByRegex("(?i).*" + name + ".*");
        List<GroupSearchedDTO> groupSearchedDTOs = new ArrayList<>();
        for (Group group : groups) {
            groupSearchedDTOs.add(new GroupSearchedDTO(group));
        }
        return groupSearchedDTOs;
    }

    public GroupDTO createGroup(GroupForm groupForm, Principal principal) {
        GroupForm completedForm = new GroupForm(groupForm);
        completedForm.setFounderUsername(principal.getName());
        Group group = dataClient.createGroup(completedForm);
        GroupDTO groupDTO = new GroupDTO(group);
        return groupDTO;
    }

    public GroupDTO getGroup(String uuid) {
        Group group = dataClient.getGroup(uuid);
        GroupDTO groupDTO = new GroupDTO(group);
        return groupDTO;
    }

    public GroupDTO editGroup(GroupForm groupForm, String uuid, Principal principal) throws NotFounderException {
        Group group = dataClient.getGroup(uuid);
        if (!group.getFounder().getUsername().equals(principal.getName())) {
            throw new NotFounderException("User " + principal.getName() + " is not the founder of group " + uuid + " and cannot edit it.");
        }
        group = dataClient.updateGroup(uuid, groupForm);
        GroupDTO groupDTO = new GroupDTO(group);
        return groupDTO;
    }

    public String deleteGroup(String uuid, Principal principal) throws NotFounderException {
        Group group = dataClient.getGroup(uuid);
        if (!group.getFounder().getUsername().equals(principal.getName())) {
            throw new NotFounderException("User " + principal.getName() + " is not the founder of group " + uuid + " and cannot delete it.");
        }
        return dataClient.deleteGroup(uuid);
    }

    public GroupDTO join(String uuid, Principal principal) {
        Group group = dataClient.joinGroup(uuid, principal.getName());
        GroupDTO groupDTO = new GroupDTO(group);
        return groupDTO;
    }

    public GroupDTO leave(String uuid, Principal principal) {
        Group group = dataClient.leaveGroup(uuid, principal.getName());
        GroupDTO groupDTO = new GroupDTO(group);
        return groupDTO;
    }

    public String adminDeleteGroup(String uuid) {
        return dataClient.deleteGroup(uuid);
    }
}
