package com.agh.EventarzDataService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewGroupDTO {
    GroupForm groupForm;
    User founder;
}
