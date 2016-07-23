package com.mccritz.kperms.commands;

import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.PlayerUtility;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class ClearPrefixCommand extends BaseCommand {

    @Command(name = "clearprefix", aliases = {"removeprefix", "deleteprefix"}, permission = "kperms.clearprefix")
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

        rank.setPrefix("");
        rank.saveRankData();
//        getRankManager().getUpdater().publish("update");

        PlayerUtility.updateTabList();

        MessageManager.sendMessage(sender, "&7The rank prefix for &c" + rank.getName() + " &7has been cleared&7.");
    }
}
