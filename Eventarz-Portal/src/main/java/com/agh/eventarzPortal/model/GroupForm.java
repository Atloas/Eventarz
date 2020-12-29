package com.agh.eventarzPortal.model;

import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

/**
 * This class is used to hold data about a soon-to-be-created Group, given by the frontend. Also contains validation logic.
 */
public class GroupForm {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;

    public GroupForm() {
    }

    /**
     * Validates the provided data.
     *
     * @return Whether the data is valid or not.
     */
    public boolean validate() {
        //name
        if (name.length() < 5 || Pattern.matches(".*[^a-zA-Z0-9\\s-:()\u0104\u0106\u0118\u0141\u0143\u00D3\u015A\u0179\u017B\u0105\u0107\u0119\u0142\u0144\u00F3\u015B\u017A\u017C.,!?$]+.*", name)) {
            return false;
        }
        //description
        if (Pattern.matches(".*[^a-zA-Z0-9\\s-:()\u0104\u0106\u0118\u0141\u0143\u00D3\u015A\u0179\u017B\u0105\u0107\u0119\u0142\u0144\u00F3\u015B\u017A\u017C.,!?$]+.*", description)) {
            return false;
        }

        return true;
    }
}
