package com.agh.EventarzGateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupForm {
    @Size(min = 5, max = 64)
    @Pattern(regexp = "[a-zA-Z0-9\\s\\-:().,!?$&*'\"]+")
    private String name;
    @Size(max = 1024)
    @Pattern(regexp = "[a-zA-Z0-9\\s\\-:().,!?$&*'\"]+")
    private String description;
    @NotBlank
    private String founderUsername;

    public GroupForm(GroupForm that) {
        this.name = that.name;
        this.description = that.description;
        this.founderUsername = that.founderUsername;
    }
}
