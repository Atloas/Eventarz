package com.agh.EventarzApplication.model.serialization;

import com.agh.EventarzApplication.model.Event;
import com.agh.EventarzApplication.model.User;
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
        gen.writeNumberField("participantCount", value.getParticipantCount());
        gen.writeBooleanField("stripped", value.isStripped());
        if (value.isStripped()) {
            gen.writeNullField("organizer");
            gen.writeArrayFieldStart("participants");
            gen.writeEndArray();
            gen.writeNullField("group");
        } else {
            gen.writeObjectField("organizer", value.getOrganizer().createStrippedCopy());
            gen.writeArrayFieldStart("participants");
            for (User user : value.getParticipants()) {
                gen.writeObject(user.createStrippedCopy());
            }
            gen.writeEndArray();
            gen.writeObjectField("group", value.getGroup().createStrippedCopy());
        }
        gen.writeEndObject();
    }
}
