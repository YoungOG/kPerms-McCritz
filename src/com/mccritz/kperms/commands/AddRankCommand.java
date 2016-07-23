package com.mccritz.kperms.commands;

import com.mccritz.kperms.ranks.Rank;
import com.mccritz.kperms.utils.MessageManager;
import com.mccritz.kperms.utils.command.BaseCommand;
import com.mccritz.kperms.utils.command.Command;
import com.mccritz.kperms.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class AddRankCommand extends BaseCommand {

    @Command(name = "addrank", aliases = {"creategroup", "createrank", "addgroup"}, permission = "kperms.addrank")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/" + command.getLabel() + " <name>");
            return;
        }

        Rank rank = getRankManager().getByName(args[0]);

        if (rank != null) {
            MessageManager.sendMessage(sender, "&7The rank &c" + args[0] + " &7already exists.");
            return;
        }

        Rank newRank = new Rank(args[0]);
        getRankManager().getRanks().add(newRank);
        newRank.saveRankData();

//        getRankManager().getUpdater().publish("update");

        MessageManager.sendMessage(sender, "&7The rank &c" + args[0] + " &7has been created.");
    }
}
