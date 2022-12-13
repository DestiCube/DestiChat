package com.desticube.chat.api.events;

import com.gamerduck.commons.events.DuckPlayerEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerAsyncChatEvent extends Event implements Cancellable {

    Component message;
    Player player;

    public PlayerAsyncChatEvent(Player player, Component message) {
        this.player = player;
        this.message = message;
    }

    public Player player() {
        return player;
    }

    public Component message() {
        return message;
    }

    public void message(Component comp) {
        message = comp;
    }

    private boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    private static HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
