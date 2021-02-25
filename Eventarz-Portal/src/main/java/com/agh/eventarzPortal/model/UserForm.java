package com.agh.eventarzPortal.model;


import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

public class UserForm {
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String repeatPassword;

    public UserForm() {
    }

    public boolean validate() {
        //username
        if (username.length() < 5 || Pattern.matches(".*[^a-zA-Z0-9]+.*", username)) {
            return false;
        }
        //password
        boolean length = password.length() >= 8;
        boolean lowerCase = Pattern.matches(".*[a-z]+.*", password);
        boolean upperCase = Pattern.matches(".*[A-Z]+.*", password);
        boolean number = Pattern.matches(".*[0-9]+.*", password);
        if (!(length && lowerCase && upperCase && number)) {
            return false;
        }
        //repeatPassword
        if (password.compareTo(repeatPassword) != 0) {
            return false;
        }

        return true;
    }
}
