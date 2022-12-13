package com.desticube.chat.commands;

import com.desticube.chat.api.exceptions.BadgeDoesntExistException;
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

public class DelBadge implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (!p.hasPermission("desticore.delbadge")) {
            p.sendMessage("No permissions");
            return false;
        }
        if (args.length <= 1) {
            p.sendMessage("/delbadge (name)");
            return false;
        }
        else {
            try {
                badgesRegistry.delBadge(args[0]);
                p.sendMessage("Badge deleted");
                return false;
            } catch (BadgeDoesntExistException e) {
                p.sendMessage("Badge doesnt exist");
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label,
                                      String[] args) {
        if (args.length == 1) {
            List<String> badges = Lists.newArrayList();
            badgesRegistry.getBadges().forEach(b -> badges.add(b.name()));
            List<String> newBadges = Lists.newArrayList();
            StringUtil.copyPartialMatches(args[0], badges, newBadges);
            return newBadges;
        } else return Collections.emptyList();
    }

}
