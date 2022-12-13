package com.desticube.chat.commands;

import com.desticube.chat.api.exceptions.BadgeDoesntExistException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.desticube.chat.ChatMain.badgesRegistry;

public class CreateBadge implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (!p.hasPermission("destichat.createbadge")) {
            p.sendMessage("No permissions to run this command");
            return false;
        }
        if (args.length <= 1) p.sendMessage("/createbadge (name) (character)");
        else {
            try {
                badgesRegistry.getBadge(args[0]);
                p.sendMessage("The badge " + args[0] + " already exists");
                return false;
            } catch (BadgeDoesntExistException e) {
                badgesRegistry.addBadge(args[0], args[1]);
                p.sendMessage("The badge " + args[0] + " has been created");
                return false;
            }
        }
        return false;
    }
}
