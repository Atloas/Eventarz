package com.agh.EventarzGateway.model;


import com.agh.EventarzGateway.model.validators.ValueMatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValueMatch(field = "password", otherField = "repeatPassword")
public class RegisterForm {
    @Size(min = 5, max = 16)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    private String username;
    @Size(min = 8, max = 128)
    @Pattern.List({
            @Pattern(regexp = ".*[A-Z].*"),
            @Pattern(regexp = ".*[a-z].*"),
            @Pattern(regexp = ".*[0-9].*")
    })
    private String password;
    @NotBlank
    private String repeatPassword;
}
