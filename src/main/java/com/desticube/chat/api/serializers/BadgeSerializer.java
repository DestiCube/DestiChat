package com.desticube.chat.api.serializers;

import com.desticube.chat.api.records.Badge;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class BadgeSerializer extends TypeAdapter<Badge> {
    @Override
    public void write(JsonWriter out, Badge badge) throws IOException {
        if (badge == null) {
            out.nullValue();
            return;
        }
        out.value(badge.name() + ";" + badge.badge() + ";" + badge.description() + ";" + badge.permission());
    }

    @Override
    public Badge read(JsonReader in) throws IOException {
        String [] parts = in.nextString().split(";");
        String name = parts[0];
        String badge = parts[1];
        String description = parts[2];
        String permission = parts[3];
        return new Badge(name, badge, description, permission);
    }
}
