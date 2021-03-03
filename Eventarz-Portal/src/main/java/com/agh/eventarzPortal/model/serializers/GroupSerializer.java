package com.agh.eventarzPortal.model.serializers;

import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.Group;
import com.agh.eventarzPortal.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class GroupSerializer extends StdSerializer<Group> {
    public GroupSerializer() {
        this(null);
    }

    public GroupSerializer(Class<Group> t) {
        super(t);
    }

    @Override
    public void serialize(Group value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeStringField("uuid", value.getUuid());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("description", value.getDescription());
        gen.writeStringField("createdDate", value.getCreatedDate());
        gen.writeArrayFieldStart("members");
        if (value.getMembers() != null) {
            for (User user : value.getMembers()) {
                gen.writeObject(user.createStrippedCopy());
            }
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("events");
        if (value.getEvents() != null) {
            for (Event event : value.getEvents()) {
                gen.writeObject(event.createStrippedCopy());
            }
        }
        gen.writeEndArray();
        if (value.getFounder() != null) {
            gen.writeObjectField("founder", value.getFounder().createStrippedCopy());
        } else {
            gen.writeNullField("founder");
        }
        gen.writeEndObject();
    }
}
