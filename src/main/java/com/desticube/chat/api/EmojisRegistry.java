package com.desticube.chat.api;

import com.desticube.chat.api.exceptions.EmojiDoesntExistException;
import com.desticube.chat.api.player.ChatPlayer;
import com.desticube.chat.api.records.Badge;
import com.desticube.chat.api.records.Emoji;
import com.desticube.chat.api.serializers.EmojiSerializer;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newCopyOnWriteArrayList;
import static java.io.File.separator;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class EmojisRegistry {

    final CopyOnWriteArrayList<Emoji> emojis = Lists.newCopyOnWriteArrayList();

    public EmojisRegistry() {

    }

    private static final Pattern HEX_PATTERN = Pattern.compile(":(.*?):");
    public String deserialize(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(buffer, getEmoji(matcher.group(1)).emojiReplacement());
            } catch (EmojiDoesntExistException e) {
                continue;
            }
        }
        return matcher.appendTail(buffer).toString();
    }

    public Component deserialize(Component text) {
        return text.replaceText((b) ->
            b.match(HEX_PATTERN).replacement(((matchResult, builder) -> {
                try {
                    return miniMessage().deserialize(getEmoji(matchResult.group(1)).emojiReplacement());
                } catch (EmojiDoesntExistException e) {
                    e.printStackTrace();
                    return miniMessage().deserialize(matchResult.group(1));
                }
            })));
    }

    public CopyOnWriteArrayList<Emoji> getEmojis() {
        return emojis;
    }

    public Emoji getEmoji(String byReplace) throws EmojiDoesntExistException {
        return emojis.stream().filter(e -> e.toReplace().equalsIgnoreCase(byReplace)).findFirst().orElseThrow(EmojiDoesntExistException::new);
    }
    public Emoji getEmojiByName(String name) throws EmojiDoesntExistException {
        return emojis.stream().filter(e -> e.name().equalsIgnoreCase(name)).findFirst().orElseThrow(EmojiDoesntExistException::new);
    }

    public void delEmoji(String name) throws EmojiDoesntExistException {
        emojis.remove(getEmojiByName(name));
    }


    public void addEmoji(String name, String toReplace, String emojiReplacement, String description, String permission) {
        emojis.add(new Emoji(name, toReplace, emojiReplacement, description, permission));
    }

    public void addEmoji(String name, String toReplace, String emojiReplacement, String description) {
        emojis.add(new Emoji(name, toReplace, emojiReplacement, description, "emojis." + name));
    }

    public void addEmoji(String name, String toReplace, String emojiReplacement) {
        emojis.add(new Emoji(name, toReplace, emojiReplacement, "A custom emoji", "emojis." + name));
    }

    public static EmojisRegistry loadEmojis(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder() + separator + "storage", "emojis.json");
        EmojisRegistry emojis = new EmojisRegistry();
        if (!file.exists()) {return emojis;}
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Emoji.class, new EmojiSerializer())
                    .create();
            Reader reader = Files.newBufferedReader(file.toPath());
            emojis = gson.fromJson(reader, EmojisRegistry.class);
            reader.close();
        } catch (Exception ex) {ex.printStackTrace();}
        return emojis;
    }


    public void saveEmojis(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder() + separator + "storage",  "emojis.json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {file.createNewFile();}
            catch (IOException e) {throw new RuntimeException(e);}
        }
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Emoji.class, new EmojiSerializer())
                    .create();
            Writer writer = Files.newBufferedWriter(file.toPath());
            gson.toJson(this, writer);
            writer.close();
        } catch (Exception ex) {ex.printStackTrace();}
    }
}
