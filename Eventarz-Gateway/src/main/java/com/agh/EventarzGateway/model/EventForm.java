package com.agh.EventarzGateway.model;

import com.agh.EventarzGateway.model.validators.FutureDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventForm {
    @Size(min = 5, max = 64)
    @Pattern(regexp = "[a-zA-Z0-9\\s\\-:().,!?$&*'\"]+")
    private String name;
    @Pattern(regexp = "[a-zA-Z0-9\\s\\-:().,!?$&*'\"]*")
    private String description;
    @Min(1)
    @Max(100)
    private int maxParticipants;
    @NotBlank
    @FutureDate
    private String eventDate;
    private boolean participate;
    // Filled with principal's name
    private String organizerUsername;
    @NotBlank
    private String groupUuid;

    public EventForm(EventForm that) {
        this.name = that.name;
        this.description = that.description;
        this.maxParticipants = that.maxParticipants;
        this.eventDate = that.eventDate;
        this.participate = that.participate;
        this.organizerUsername = that.organizerUsername;
        this.groupUuid = that.groupUuid;
    }
}
