package com.mccritz.kperms.commands;

import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.PlayerUtility;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetPrefixCommand extends BaseCommand {

    @Command(name = "setprefix", aliases = {"addprefix"}, permission = "kperms.setprefix")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <rank> <prefix>");
            return;
        }

        Rank rank = getRankManager().getByName(args[0]);

        if (rank == null) {
            MessageManager.sendMessage(sender, "&7The rank &c" + args[0] + " &7does not exist.");
            return;
        }

        rank.setPrefix(args[1]);
        rank.saveRankData();

//        getRankManager().getUpdater().publish("update");

        PlayerUtility.updateTabList();

        sender.sendMessage(ChatColor.GRAY + "The rank prefix for " + ChatColor.RED + rank.getName() + ChatColor.GRAY + " has been set to " + ChatColor.RED + args[1] + ChatColor.GRAY + ".");
    }
}
