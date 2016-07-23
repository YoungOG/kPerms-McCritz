package com.mccritz.kperms.commands;

import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListPermissionsCommand extends BaseCommand {

    @Command(name = "listpermissions", aliases = {"listperms"}, permission = "kperms.listpermissions")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <rank>");
            return;
        }

        Rank rank = getRankManager().getByName(args[0]);

        if (rank == null) {
            MessageManager.sendMessage(sender, "&7The rank &c" + args[0] + " &7does not exist.");
            return;
        }

        if (rank.getPermissions().size() > 0) {
            MessageManager.sendMessage(sender, "&7The rank &c" + rank.getName() + " &7has access to the following permissions:");

            for (String permissions : rank.getPermissions()) {
                MessageManager.sendMessage(sender, "&c- " + permissions);
            }
        } else {
            MessageManager.sendMessage(sender, "&7The rank &c" + rank.getName() + " &7does not have any permissions.");
        }
    }
}
