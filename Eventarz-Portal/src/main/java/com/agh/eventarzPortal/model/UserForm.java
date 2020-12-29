package com.agh.eventarzPortal.model;


import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

/**
 * This class is used to hold data about a soon-to-be-created User, given by the frontend. Also contains validation logic.
 */
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

    /**
     * Validates the provided data.
     *
     * @return Whether the data is valid or not.
     */
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
