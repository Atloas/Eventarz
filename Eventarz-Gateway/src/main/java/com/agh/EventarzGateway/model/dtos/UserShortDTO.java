package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDTO {
    private String username;

    public UserShortDTO(User user) {
        this.username = user.getUsername();
    }
}
