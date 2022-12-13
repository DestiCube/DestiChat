package com.desticube.chat.commands;

import com.desticube.chat.ChatMain;
import com.desticube.chat.api.player.ChatPlayer;
import com.desticube.chat.api.records.Badge;
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

import static com.desticube.chat.ChatMain.badgesRegistry;
import static com.desticube.chat.ChatMain.playersRegistry;
import static com.gamerduck.commons.general.Components.translate;
import static com.gamerduck.commons.general.Strings.color;
import static net.kyori.adventure.text.Component.space;

public class Badges implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        openGUI((Player) sender);
        return false;
    }

    private void openGUI(Player p) {
        ChatPlayer cp = playersRegistry.getPlayer(p);
        List<Badge> badges = badgesRegistry.getBadges().stream().filter(b -> p.hasPermission(b.permission())).collect(Collectors.toList());
        DuckInventory inv = new DuckInventory(ChatMain.instance, 54, "<yellow><b>Badges</b>")
                .fillBorder(new DuckItem().withMaterial(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(" "));
        for (Badge badge : badges) {
            inv.addButton(new DuckItem()
                            .withMaterial(Material.NAME_TAG)
                            .withDisplayName(translate("<white>" + badge.name()))
                            .addToLore(translate("<gray>This badge will be displayed"), translate("<gray>next to your rank in game"),
                                    translate(" <gray>» " + badge.description()), space(), translate("<gray>Click to set as your current badge!"))
                            .withFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                            .withEnchant(Enchantment.SILK_TOUCH, 1),
                    (event) -> {
                        cp.setCurrentBadge(badgesRegistry.getBadge(badge.name()));
                        p.closeInventory();
                        p.sendMessage("Badge Set");
                    });
        }
        inv.open(p);
    }
}
