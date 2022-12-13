package com.desticube.chat.commands;

import com.desticube.chat.ChatMain;
import com.desticube.chat.api.player.ChatPlayer;
import com.desticube.chat.api.records.Badge;
import com.desticube.chat.api.records.Emoji;
import com.gamerduck.commons.inventory.DuckInventory;
import com.gamerduck.commons.items.DuckItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.stream.Collectors;

import static com.desticube.chat.ChatMain.*;
import static com.gamerduck.commons.general.Components.translate;
import static com.gamerduck.commons.general.Strings.color;
import static net.kyori.adventure.text.Component.space;

public class Emojis implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        openGUI((Player) sender);
        return false;
    }

    private void openGUI(Player p) {
        List<Emoji> emojis = emojisRegistry.getEmojis().stream().filter(b -> p.hasPermission(b.permission())).collect(Collectors.toList());
        DuckInventory inv = new DuckInventory(ChatMain.instance, 54, "<yellow><b>Emojis</b>")
                .fillBorder(new DuckItem().withMaterial(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(" "));
        for (Emoji emoji : emojis) {
            inv.addItem(new DuckItem()
                            .withMaterial(Material.SUNFLOWER)
                            .withDisplayName(translate("<white>" + emoji.name()))
                            .addToLore(translate("<gray>This emoji can be used in your chat using " + emoji.toReplace()))
                            .withFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                            .withEnchant(Enchantment.SILK_TOUCH, 1));
        }
        inv.shouldClickBeCancelled(true);
        inv.open(p);
    }
}
