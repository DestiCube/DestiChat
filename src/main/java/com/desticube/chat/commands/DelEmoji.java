package com.desticube.chat.commands;

import com.desticube.chat.api.exceptions.BadgeDoesntExistException;
import com.desticube.chat.api.exceptions.EmojiDoesntExistException;
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

import static com.desticube.chat.ChatMain.badgesRegistry;
import static com.desticube.chat.ChatMain.emojisRegistry;

public class DelEmoji implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (!p.hasPermission("desticore.delbadge")) {
            p.sendMessage("No permissions");
            return false;
        }
        if (args.length <= 1) {
            p.sendMessage("/delemoji (name)");
            return false;
        }
        else {
            try {
                emojisRegistry.delEmoji(args[0]);
                p.sendMessage("Emoji deleted");
                return false;
            } catch (EmojiDoesntExistException e) {
                p.sendMessage("Emoji doesnt exist");
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label,
                                      String[] args) {
        if (args.length == 1) {
            List<String> badges = Lists.newArrayList();
            emojisRegistry.getEmojis().forEach(b -> badges.add(b.name()));
            List<String> newBadges = Lists.newArrayList();
            StringUtil.copyPartialMatches(args[0], badges, newBadges);
            return newBadges;
        } else return Collections.emptyList();
    }

}
