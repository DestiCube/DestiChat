package com.desticube.chat.api.hooks;

import com.desticube.chat.api.player.ChatPlayer;
import com.desticube.placeholders.api.PlaceholderExtension;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

import static com.desticube.chat.ChatMain.defaultBadge;
import static com.desticube.chat.ChatMain.playersRegistry;

public class ChatExtension extends PlaceholderExtension {
    @Override
    public String getAuthor() {
        return "GamerDuck123";
    }

    @Override
    public String getIdentifier() {
        return "destichat";
    }

    @Override
    public String getVersion() {
        return "Eh-Who-Cares";
    }

    @Override
    public CompletableFuture<String> onRequest(Player player, String params) {
        return CompletableFuture.supplyAsync(() -> {
            ChatPlayer p = playersRegistry.getPlayer(player.getUniqueId());
            if (params.equalsIgnoreCase("badge")) {
                if (p.getCurrentBadge() == null) p.setCurrentBadge(defaultBadge);
                return p.getCurrentBadge().badge();
            }
            return params;
        });
    }
}