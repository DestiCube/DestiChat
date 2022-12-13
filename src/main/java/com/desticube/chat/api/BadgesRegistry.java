package com.desticube.chat.api;

import com.desticube.chat.api.exceptions.BadgeDoesntExistException;
import com.desticube.chat.api.exceptions.EmojiDoesntExistException;
import com.desticube.chat.api.records.Badge;
import com.desticube.chat.api.records.Emoji;
import com.desticube.chat.api.serializers.BadgeSerializer;
import com.gamerduck.commons.items.DuckItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.collect.Lists.newCopyOnWriteArrayList;
import static java.io.File.separator;

public class BadgesRegistry {

    final CopyOnWriteArrayList<Badge> badges = newCopyOnWriteArrayList();

    public BadgesRegistry() {

    }

    public CopyOnWriteArrayList<Badge> getBadges() {
        return badges;
    }

    public Badge getBadge(String name) throws BadgeDoesntExistException {
        return badges.stream().filter(e -> e.name().equalsIgnoreCase(name)).findFirst().orElseThrow(BadgeDoesntExistException::new);
    }

    public void delBadge(String name) throws BadgeDoesntExistException {
        badges.remove(getBadge(name));
    }

    public void addBadge(String name, String badge, String description, String permission) {
        badges.add(new Badge(name, badge, description, permission));
    }

    public void addBadge(String name, String badge, String description) {
        badges.add(new Badge(name, badge, description, "badges." + name));
    }

    public void addBadge(String name, String badge) {
        badges.add(new Badge(name, badge, "This is a badge", "badges." + name));
    }


    public static BadgesRegistry loadBadges(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder() + separator + "storage", "badges.json");
        BadgesRegistry badges = new BadgesRegistry();
        if (!file.exists()) {return badges;}
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Badge.class, new BadgeSerializer())
                    .create();
            Reader reader = Files.newBufferedReader(file.toPath());
            badges = gson.fromJson(reader, BadgesRegistry.class);
            reader.close();
        } catch (Exception ex) {ex.printStackTrace();}
        return badges;
    }


    public void saveBadges(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder() + separator + "storage",  "badges.json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {file.createNewFile();}
            catch (IOException e) {throw new RuntimeException(e);}
        }
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Badge.class, new BadgeSerializer())
                    .create();
            Writer writer = Files.newBufferedWriter(file.toPath());
            gson.toJson(this, writer);
            writer.close();
        } catch (Exception ex) {ex.printStackTrace();}
    }

}
