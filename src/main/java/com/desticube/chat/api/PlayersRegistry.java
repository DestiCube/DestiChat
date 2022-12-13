package com.desticube.chat.api;

import com.desticube.chat.api.player.ChatPlayer;
import com.desticube.chat.api.records.Emoji;
import com.google.common.collect.Maps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.desticube.chat.api.player.ChatPlayer.loadPlayer;
import static com.google.common.collect.Lists.newCopyOnWriteArrayList;
import static com.google.common.collect.Maps.newConcurrentMap;
import static org.bukkit.Bukkit.getScheduler;

public class PlayersRegistry implements Listener {

    final ConcurrentHashMap<UUID, ChatPlayer> players = new ConcurrentHashMap<>();
    final JavaPlugin plugin;

    public PlayersRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Stream<ChatPlayer> getPlayers() {
        return players.values().stream();
    }
    public ChatPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public ChatPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        getScheduler().runTaskAsynchronously(plugin, () -> players.put(id, loadPlayer(plugin, id)));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        getScheduler().runTaskAsynchronously(plugin, () -> {
            players.get(id).savePlayer(plugin);
            players.remove(id);
        });
    }
}
