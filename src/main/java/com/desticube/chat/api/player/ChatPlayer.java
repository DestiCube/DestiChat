package com.desticube.chat.api.player;

import com.desticube.chat.api.records.Badge;
import com.desticube.chat.api.serializers.BadgeSerializer;
import com.desticube.chat.api.serializers.UUIDSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.UUID;

import static java.io.File.separator;

public class ChatPlayer {

    public final UUID uniqueID;
    public Badge currentBadge;

    public ChatPlayer(UUID uuid, Badge currentBadge) {
        this.uniqueID = uuid;
        this.currentBadge = currentBadge;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public Badge getCurrentBadge() {
        return currentBadge;
    }
    public void setCurrentBadge(Badge badge) {
        currentBadge = badge;
    }

    public static ChatPlayer loadPlayer(JavaPlugin plugin, UUID uniqueID) {
        File file = new File(plugin.getDataFolder() + separator + "storage" + separator + "players", uniqueID.toString() + ".json");
        ChatPlayer player = new ChatPlayer(uniqueID, null);
        if (!file.exists()) {return player;}
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(UUID.class, new UUIDSerializer())
                    .registerTypeAdapter(Badge.class, new BadgeSerializer())
                    .create();
            Reader reader = Files.newBufferedReader(file.toPath());
            player = gson.fromJson(reader, ChatPlayer.class);
            reader.close();
        } catch (Exception ex) {ex.printStackTrace();}
        return player;
    }


    public void savePlayer(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder() + separator + "storage" + separator + "players", uniqueID.toString() + ".json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {file.createNewFile();}
            catch (IOException e) {throw new RuntimeException(e);}
        }
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(UUID.class, new UUIDSerializer())
                    .registerTypeAdapter(Badge.class, new BadgeSerializer())
                    .create();
            Writer writer = Files.newBufferedWriter(file.toPath());
            gson.toJson(this, writer);
            writer.close();
        } catch (Exception ex) {ex.printStackTrace();}
    }
}
