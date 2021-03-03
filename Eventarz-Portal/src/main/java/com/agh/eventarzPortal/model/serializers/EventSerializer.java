package com.agh.eventarzPortal.model.serializers;

import com.agh.eventarzPortal.model.Event;
import com.agh.eventarzPortal.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class EventSerializer extends JsonSerializer<Event> {

    @Override
    public void serialize(Event value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeStringField("uuid", value.getUuid());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("description", value.getDescription());
        gen.writeNumberField("maxParticipants", value.getMaxParticipants());
        gen.writeStringField("eventDate", value.getEventDate());
        gen.writeStringField("publishedDate", value.getPublishedDate());
        gen.writeBooleanField("expired", value.isExpired());
        if (value.getOrganizer() != null) {
            gen.writeObjectField("organizer", value.getOrganizer().createStrippedCopy());
        } else {
            gen.writeNullField("organizer");
        }
        gen.writeArrayFieldStart("participants");
        if (value.getParticipants() != null) {
            for (User user : value.getParticipants()) {
                gen.writeObject(user.createStrippedCopy());
            }
        }
        gen.writeEndArray();
        if (value.getGroup() != null) {
            gen.writeObjectField("group", value.getGroup().createStrippedCopy());
        } else {
            gen.writeNullField("group");
        }
        gen.writeEndObject();
    }
}
