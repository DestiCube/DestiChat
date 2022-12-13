package com.desticube.chat.api.serializers;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.UUID;

public class UUIDSerializer extends TypeAdapter<UUID> {
    @Override
    public void write(JsonWriter out, UUID uuid) throws IOException {
        if (uuid == null) {
            out.nullValue();
            return;
        }
        out.value(uuid.toString());
    }

    @Override
    public UUID read(JsonReader in) throws IOException {
        return UUID.fromString(in.nextString());
    }
}
