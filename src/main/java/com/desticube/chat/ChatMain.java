package com.desticube.chat;

import com.desticube.chat.api.BadgesRegistry;
import com.desticube.chat.api.EmojisRegistry;
import com.desticube.chat.api.PlayersRegistry;
import com.desticube.chat.api.events.PlayerAsyncChatEvent;
import com.desticube.chat.api.exceptions.EmojiDoesntExistException;
import com.desticube.chat.api.hooks.ChatExtension;
import com.desticube.chat.api.player.ChatPlayer;
import com.desticube.chat.api.records.Badge;
import com.desticube.chat.commands.*;
import com.desticube.placeholders.api.Placeholders;
import com.gamerduck.commons.general.ColorTranslator;
import com.gamerduck.commons.general.Components;
import com.gamerduck.commons.general.Numbers;
import com.gamerduck.commons.general.Strings;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.desticube.placeholders.api.Placeholders.setPlaceholders;
import static com.gamerduck.commons.general.Components.*;
import static com.gamerduck.commons.general.Numbers.toRoman;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;
import static org.bukkit.Bukkit.broadcast;
import static org.bukkit.Bukkit.getScheduler;

public class ChatMain extends JavaPlugin implements Listener {

    public static ChatMain instance;

    public static BadgesRegistry badgesRegistry;
    public static EmojisRegistry emojisRegistry;
    public static PlayersRegistry playersRegistry;
    public static Badge defaultBadge;
    public static YamlConfiguration config;
    Component chatFormat;

    @Override
    public void onEnable() {
        instance = this;
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdir();
            saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        try {config.load(configFile);}
        catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
        if (Bukkit.getPluginManager().getPlugin("DestiPlaceholders") != null) Placeholders.register(new ChatExtension());
        badgesRegistry = BadgesRegistry.loadBadges(instance);
        emojisRegistry = EmojisRegistry.loadEmojis(instance);
        playersRegistry = new PlayersRegistry(instance);

        chatFormat = miniMessage().deserialize(config.getString("Chat-Format"));
        defaultBadge = badgesRegistry.getBadge(config.getString("Default-Badge"));
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("badges").setExecutor(new Badges());
        getCommand("createbadge").setExecutor(new CreateBadge());
        getCommand("delbadge").setExecutor(new DelBadge());
        getCommand("emojis").setExecutor(new Emojis());
        getCommand("createemoji").setExecutor(new CreateEmoji());
        getCommand("delemoji").setExecutor(new DelEmoji());
    }

    @Override
    public void onDisable() {
        badgesRegistry.saveBadges(instance);
        emojisRegistry.saveEmojis(instance);
        playersRegistry.getPlayers().forEach(p -> p.savePlayer(instance));
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        getScheduler().runTaskAsynchronously(this, () -> {
            PlayerAsyncChatEvent event = new PlayerAsyncChatEvent(e.getPlayer(), e.message());
            if (!event.isCancelled()) {
                broadcast(miniMessage().deserialize(setPlaceholders(event.player(), miniMessage().serialize(
                        formatCommand(formatItem(event.player().getInventory().getItemInMainHand(), chatFormat.replaceText((b) -> b.match("%message%")
                                .replacement(emojisRegistry.deserialize(event.message())))))))));
            }
        });
        e.setCancelled(true);
    }

    private final Pattern ITEM_PATTERN = Pattern.compile("\\[(item)\\]");
    private Component formatItem(ItemStack item, Component text) {
        return text.replaceText((b) ->
                b.match(ITEM_PATTERN).replacement((matchResult, builder) -> {
                            TextComponent.Builder build = Component.text().hoverEvent(item);
                            String[] name = item.getType().toString().toLowerCase().split("_");
                            for (String s : name) build.append(text(Strings.capitalizeFirst(s))).append(Component.space());
                            if (item.getAmount() > 1) build.append(text("x" + item.getAmount()));
                            return build.color(TextColor.color(255, 255, 100)).build();
                        }
                ));
    }

    private final Pattern COMMAND = Pattern.compile("\\[(\\/.*?)\\]");
    private Component formatCommand(Component text) {
        return text.replaceText((b) ->
                b.match(COMMAND).replacement((matchResult, builder) -> {
                            String command = matchResult.group(1);
                            TextComponent.Builder build = Component.text().content("[" + command + "]");
                            build.hoverEvent(text("Click to run this command!"));
                            build.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
                            return build.color(TextColor.color(255, 255, 100)).build();
                        }
                ));
    }

}