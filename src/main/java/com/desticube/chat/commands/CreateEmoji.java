package com.desticube.chat.commands;

import com.desticube.chat.api.exceptions.BadgeDoesntExistException;
import com.desticube.chat.api.exceptions.EmojiDoesntExistException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.desticube.chat.ChatMain.badgesRegistry;
import static com.desticube.chat.ChatMain.emojisRegistry;

public class CreateEmoji implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (!p.hasPermission("destichat.createbadge")) {
            p.sendMessage("No permissions to run this command");
            return false;
        }
        if (args.length <= 2) p.sendMessage("/createemoji (name) (toreplace) (replacement)");
        else {
            try {
                emojisRegistry.getEmojiByName(args[0]);
                p.sendMessage("The emoji " + args[0] + " already exists");
                return false;
            } catch (EmojiDoesntExistException e) {
                emojisRegistry.addEmoji(args[0], args[1], args[2]);
                p.sendMessage("The emoji " + args[0] + " has been created");
                return false;
            }
        }
        return false;
    }
}
