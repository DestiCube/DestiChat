package com.desticube.chat.api.serializers;


import com.desticube.chat.api.records.Badge;
import com.desticube.chat.api.records.Emoji;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class EmojiSerializer extends TypeAdapter<Emoji> {
    @Override
    public void write(JsonWriter out, Emoji emoji) throws IOException {
        if (emoji == null) {
            out.nullValue();
            return;
        }
        out.value(emoji.name() + ";" + emoji.toReplace() + ";" + emoji.emojiReplacement() + ";" + emoji.description() + ";" + emoji.permission());
    }

    @Override
    public Emoji read(JsonReader in) throws IOException {
        String [] parts = in.nextString().split(";");
        String name = parts[0];
        String toReplace = parts[1];
        String emojiReplacement = parts[2];
        String description = parts[3];
        String permission = parts[4];
        return new Emoji(name, toReplace, emojiReplacement, description, permission);
    }
}