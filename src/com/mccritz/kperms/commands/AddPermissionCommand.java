package com.mccritz.kperms.commands;

import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.PlayerUtility;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class AddPermissionCommand extends BaseCommand {

    @Command(name = "addpermission", aliases = {"addperm"}, permission = "kperms.addpermission")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <rank> <permission>");
            return;
        }

        Rank rank = getRankManager().getByName(args[0]);

        if (rank == null) {
            MessageManager.sendMessage(sender, "&7The rank &c" + args[0] + " &7does not exist.");
            return;
        }

        if (rank.getPermissions().contains(args[1])) {
            MessageManager.sendMessage(sender, "&7The rank &c" + rank.getName() + " &7already has access to that permission.");
            return;
        }

        rank.getPermissions().add(args[1]);
        rank.saveRankData();

//        getRankManager().getUpdater().publish("update");

        PlayerUtility.updatePermissions(rank);

        MessageManager.sendMessage(sender, "&7You have added the permission &c" + args[1] + " &7to the rank &c" + rank.getName() + "&7.");
    }
}
