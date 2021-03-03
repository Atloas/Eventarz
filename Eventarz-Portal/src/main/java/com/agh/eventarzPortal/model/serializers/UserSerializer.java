package com.agh.eventarzPortal.model.serializers;

import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.Group;
import com.agh.eventarzPortal.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class UserSerializer extends StdSerializer<User> {
    public UserSerializer() {
        this(null);
    }

    public UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeStringField("uuid", value.getUuid());
        gen.writeStringField("username", value.getUsername());
        gen.writeStringField("registerDate", value.getRegisterDate());
        gen.writeObjectField("securityDetails", value.getSecurityDetails());
        gen.writeArrayFieldStart("events");
        if (value.getEvents() != null) {
            for (Event event : value.getEvents()) {
                gen.writeObject(event.createStrippedCopy());
            }
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("organizedEvents");
        if (value.getOrganizedEvents() != null) {
            for (Event organizedEvent : value.getOrganizedEvents()) {
                gen.writeObject(organizedEvent.createStrippedCopy());
            }
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("groups");
        if (value.getGroups() != null) {
            for (Group group : value.getGroups()) {
                gen.writeObject(group.createStrippedCopy());
            }
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("foundedGroups");
        if (value.getFoundedGroups() != null) {
            for (Group foundedGroup : value.getFoundedGroups()) {
                gen.writeObject(foundedGroup.createStrippedCopy());
            }
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
