package com.agh.EventarzApplication.model.serialization;

import com.agh.EventarzApplication.model.Event;
import com.agh.EventarzApplication.model.Group;
import com.agh.EventarzApplication.model.User;
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
        gen.writeBooleanField("stripped", value.isStripped());
        if (value.isStripped()) {
            gen.writeArrayFieldStart("members");
            gen.writeEndArray();
            gen.writeArrayFieldStart("events");
            gen.writeEndArray();
            gen.writeNullField("founder");
        } else {
            gen.writeArrayFieldStart("members");
            for (User user : value.getMembers()) {
                gen.writeObject(user.createStrippedCopy());
            }
            gen.writeEndArray();
            gen.writeArrayFieldStart("events");
            for (Event event : value.getEvents()) {
                gen.writeObject(event.createStrippedCopy());
            }
            gen.writeEndArray();
            gen.writeObjectField("founder", value.getFounder().createStrippedCopy());

        }
        gen.writeEndObject();
    }
}
