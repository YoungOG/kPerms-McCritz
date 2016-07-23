package com.mccritz.kperms.commands;

import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HasPermissionCommand extends BaseCommand {

    @Command(name = "haspermission", aliases = {"checkperm", "checkpermission", "hasperm"}, permission = "kperms.haspermission")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <player> <permission>");
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            MessageManager.sendMessage(sender, MessageManager.PLAYER_NOT_FOUND.replace("%player%", args[0]));
            return;
        }

        MessageManager.sendMessage(sender, "&7The user &c" + player.getName() + " " + (player.hasPermission(args[1]) ? "&7has permission to &c" : "&7does not have permission to &c") + args[1] + "&7.");
    }
}
